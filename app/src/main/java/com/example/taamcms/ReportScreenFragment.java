package com.example.taamcms;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportScreenFragment extends LoaderFragment {
    private final GeneratePDFMode[] selectModes = {
            new GeneratePDFMode(
                    "Lot Number",
                    R.string.hint_lot_number,
                    true,
                    new DisplayItemStringFilter() {
                        @Override
                        public boolean isWanted(DisplayItem item, String target) {
                            if (item.getLot() == null) {
                                return false;
                            }

                            return item.getLot().toLowerCase().equals(target);
                        }
                    }
            ),
            new GeneratePDFMode(
                    "Name",
                    R.string.hint_name,
                    true,
                    new DisplayItemStringFilter() {
                        @Override
                        public boolean isWanted(DisplayItem item, String target) {
                            if (item.getTitle() == null) {
                                return false;
                            }

                            return item.getTitle().toLowerCase().equals(target);
                        }
                    }
            ),
            new GeneratePDFMode(
                    "Category",
                    R.string.hint_category,
                    true,
                    new DisplayItemStringFilter() {
                        @Override
                        public boolean isWanted(DisplayItem item, String target) {
                            if (item.getCategory() == null) {
                                return false;
                            }

                            return item.getCategory().toLowerCase().equals(target);
                        }
                    }
            ),
            new GeneratePDFMode(
                    "Period",
                    R.string.hint_period,
                    true,
                    new DisplayItemStringFilter() {
                        @Override
                        public boolean isWanted(DisplayItem item, String target) {
                            if (item.getPeriod() == null) {
                                return false;
                            }
                            return item.getPeriod().toLowerCase().equals(target);
                        }
                    }
            ),
            new GeneratePDFMode(
                    "For All Items",
                    -1,
                    false,
                    new DisplayItemStringFilter() {
                        @Override
                        public boolean isWanted(DisplayItem item, String target) {
                            return true;
                        }
                    }
            )
    };

    private FirebaseDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.report_screen_fragment, container, false);

        // Allow the network fetching on the main thread.
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Connect to the db
        db = FirebaseDatabase.getInstance("https://taam-collection-default-rtdb.firebaseio.com/");

        // Set up the spinner
        Spinner modeSelector = view.findViewById(R.id.generateReportMethodSelectSpinner);
        ArrayAdapter<GeneratePDFMode> adapter = new ArrayAdapter<GeneratePDFMode>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                selectModes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSelector.setAdapter(adapter);

        // Handle spinner on change.
        modeSelector.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView adapter, View v, int position, long id) {
                        GeneratePDFMode mode = selectModes[position];

                        // Show/hide the text input.
                        EditText textInput = view.findViewById(R.id.generateReportTextInput);
                        textInput.setText("");
                        if (mode.requireTextInput) {
                            textInput.setVisibility(View.VISIBLE);
                            textInput.setHint(mode.hintId);
                        } else {
                            textInput.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView adapter) {

                    }
                }
        );

        Button generateReportButton = view.findViewById(R.id.generateReportButton);

        generateReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateReportButtonPressed(view, (GeneratePDFMode)modeSelector.getSelectedItem());
            }
        });

        return view;
    }

    /**
     * Handles the button press of generate PDF.
     * @param view of the main screen, NOT the button.
     */
    private void generateReportButtonPressed(View view, GeneratePDFMode mode) {
        // Check that the text input has been filled.
        EditText textInput = view.findViewById(R.id.generateReportTextInput);
        TextView messageDisplay = view.findViewById(R.id.generateReportMessageDisplay);

        // Input text is case insensitive for ease of use.
        String inputText = String.valueOf(textInput.getText()).toLowerCase();
        // Display an error if the text input is empty but we require a filter.
        if (inputText.isEmpty() && mode.requireTextInput) {
            messageDisplay.setText("");
            textInput.setError(String.format(
                    getResources().getString(R.string.generate_report_input_empty_error),
                    mode.dropDownTitle.toLowerCase()
            ));
            textInput.requestFocus();
            return;
        }

        // Proceed with PDF generation.
        CheckBox imageDescriptionOnlyCheckbox = view.findViewById(R.id.generateReportWithPDOnlyCheckBox);
        Button generateReportButton = view.findViewById(R.id.generateReportButton);

        // Display the generating message.
        messageDisplay.setText(R.string.generating_report);
        textInput.setError(null);

        // Disable the button from being clicked.
        generateReportButton.setClickable(false);

        // Fetch the resource from the database.
        DatabaseReference items = db.getReference("Displays/");
        items.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReportPDFGenerator pdfGenerator = new ReportPDFGenerator(
                        imageDescriptionOnlyCheckbox.isChecked()
                );

                // Query the resource for the respective filter.
                for (DataSnapshot child : snapshot.getChildren()) {
                    DisplayItem item = child.getValue(DisplayItem.class);

                    if (mode.filter.isWanted(item, inputText)) {
                        pdfGenerator.addItem(item);
                    }
                }

                // Generate pdf.
                String dateValue = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss", Locale.getDefault()).format(new Date());
                String fileName = "TAAM_collection_report_" + dateValue + ".pdf";
                boolean success = pdfGenerator.generateReport(getContext(), fileName);

                // Re-enable button and display success message.
                generateReportButton.setClickable(true);
                if (success) {
                    messageDisplay.setText(String.format(
                            getResources().getString(R.string.generate_report_success),
                            fileName
                    ));
                } else {
                    messageDisplay.setText(R.string.unknown_error);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Show an error and allow for the button to be clicked again.
                generateReportButton.setClickable(true);
                messageDisplay.setText(R.string.generate_report_database_error);
            }
        });



    }
}
