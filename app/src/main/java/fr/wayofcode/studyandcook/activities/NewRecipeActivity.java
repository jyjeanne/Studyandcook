package fr.wayofcode.studyandcook.activities;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.List;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.fragments.BackConfirmationFragment;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;
import fr.wayofcode.studyandcook.R;
import fr.wayofcode.studyandcook.databases.DbHelperRecipes;

public class NewRecipeActivity extends AppCompatActivity implements VerticalStepperForm {

    // Constantes
    private static final int CAMERA_PIC_REQUEST = 22;

    public static final String NEW_RECIPE_ADDED = "new_recipe_added";

    // Information about the steps/fields of the form
    private static final int RECIPE_PICTURE_STEP_NUM = 0;
    private static final int RECIPE_TITLE_STEP_NUM = 1;
    private static final int TIME_STEP_NUM = 2;
    private static final int SUMMARY_DESCRIPTION_STEP_NUM = 3;
    private static final int NUMBER_PERSON_STEP_NUM = 4;
    private static final int RECIPE_CATEGORIE_STEP_NUM = 5;
    private static final int PRICE_STEP_NUM = 6;
    private static final int INGREDIENT_DESCRIPTION_STEP_NUM = 7;
    private static final int DIRECTIONS_DESCRIPTION_STEP_NUM = 8;

    // Database
    DbHelperRecipes mDBhelperRecipes;

    // Recipe picture step
    private ImageView mImgImageRecipe;
    private ImageView mImgViewButton;
    public static final String STATE_RECIPE_PICTURE = "picture";

    // Recipe title step
    private EditText recipeNameEditText;
    private static final int MIN_CHARACTERS_RECIPE_NAME = 3;
    public static final String STATE_RECIPE_NAME = "title";

    // Recipe categories Step
    private TextView categoriesTextView;
    private Spinner categoriesSpinner;
    private AlertDialog categoriesDialog;
    public static final String STATE_RECIPE_CATEGORIES = "categorie";

    // Cook time step
    private TextView timeTextView;
    private TimePickerDialog timePicker;
    private Pair<Integer, Integer> time;
    public static final String STATE_TIME_HOUR = "time_hour";
    public static final String STATE_TIME_MINUTES = "time_minutes";

    // Number person step
    private TextView nbPersonTextView;
    private AlertDialog numberPersonDialog ;
    private NumberPicker numberPersonPicker;
    private static final int MIN_PERSON_NUMBER = 1;
    private static final int MAX_PERSON_NUMBER = 10;
    public static final String STATE_PERSON_NUMBER = "person_number";

    // Recipe price step
    private double recipePrice;
    private double firstDigit;
    private double secondDigit;
    private double thirdDigit;
    private double fourthDigit;
    private TextView priceTextView;
    private LinearLayout priceLinearLayout;
    private AlertDialog priceRecipeDialog ;
    private NumberPicker firstNumberPicker;
    private NumberPicker secondNumberPicker;
    private NumberPicker thirdNumberPicker;
    private NumberPicker fourthNumberPicker;
    private static final int MIN_PRICE_NUMBER = 0;
    private static final int MAX_PRICE_NUMBER = 9;
    public static final String STATE_PRICE = "price";

    // Summary description step
    private EditText descriptionEditText;
    private static final int MIN_CHARACTERS_RECIPE_DESCRIPTION = 3;
    public static final String STATE_DESCRIPTION = "description";


    private boolean confirmBack = true;
    private ProgressDialog progressDialog;
    private VerticalStepperFormLayout verticalStepperForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical_stepper_form);

        initializeActivity();

    }

    private void initializeActivity() {

        // initialize database
        mDBhelperRecipes = new DbHelperRecipes(this);
        mDBhelperRecipes.openDataBase();

        // Set toolbar as actionbar and up navigation button and set toolbar title to null
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_logo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set Recipe image View
        this.mImgImageRecipe = (ImageView) this.findViewById(R.id.image_recipe);

        // Time step vars
        time = new Pair<>(8, 30);
        setTimePicker(8, 30);

        // Number person step vars
        setPersonNumberPicker(MIN_PERSON_NUMBER,MAX_PERSON_NUMBER);

        // Categories step vars
        setCategoriesSpinner();

        // Price step vars
        setPricePickers(MIN_PRICE_NUMBER,MAX_PRICE_NUMBER);

        // Vertical Stepper form vars
        int colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.primary_color);
        int colorPrimaryDark = ContextCompat.getColor(getApplicationContext(), R.color.dark_primary_color);
        String[] stepsNames = getResources().getStringArray(R.array.steps_names);

        // Here we find and initialize the form
        verticalStepperForm = (VerticalStepperFormLayout) findViewById(R.id.vertical_stepper_form);
        VerticalStepperFormLayout.Builder.newInstance(verticalStepperForm, stepsNames, this, this)
                .primaryColor(colorPrimary)
                .primaryDarkColor(colorPrimaryDark)
                .displayBottomNavigation(true)
                .init();

    }

    // METHODS THAT HAVE TO BE IMPLEMENTED TO MAKE THE LIBRARY WORK
    // (Implementation of the interface "VerticalStepperForm")

    @Override
    public View createStepContentView(int stepNumber) {
        // Here we generate the content view of the correspondent step and we return it so it gets
        // automatically added to the step layout (AKA stepContent)
        View view = null;
        switch (stepNumber) {
            case RECIPE_PICTURE_STEP_NUM:
                view = createRecipePictureStep();
                break;
            case RECIPE_TITLE_STEP_NUM:
                view = createRecipeTitleStep();
                break;
            case TIME_STEP_NUM:
                view = createRecipeTimeStep();
                break;
            case NUMBER_PERSON_STEP_NUM:
                view = createPersonNumberStep();
                break;
            case SUMMARY_DESCRIPTION_STEP_NUM:
                view = createRecipeDescriptionStep();
                break;
            case RECIPE_CATEGORIE_STEP_NUM:
                view = createCategoriesStep();
                break;
            case PRICE_STEP_NUM:
                view = createPriceStep();
                break;
        }
        return view;
    }

    @Override
    public void onStepOpening(int stepNumber) {
        switch (stepNumber) {
            case RECIPE_TITLE_STEP_NUM:
                // When this step is open, we check that the title is correct
                checkTitleStep(recipeNameEditText.getText().toString());
                break;
            case SUMMARY_DESCRIPTION_STEP_NUM:
            case TIME_STEP_NUM:
                // As soon as they are open, these two steps are marked as completed because they
                // have default values
                verticalStepperForm.setStepAsCompleted(stepNumber);
                // In this case, the instruction above is equivalent to:
                // verticalStepperForm.setActiveStepAsCompleted();
                break;
        }
    }

    @Override
    public void sendData() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.show();
        progressDialog.setMessage(getString(R.string.vertical_form_stepper_form_sending_data_message));
        executeDataSending();
    }

    // OTHER METHODS USED TO MAKE THIS EXAMPLE WORK

    private void executeDataSending() {

        // TODO Use here the data of the form as you wish

        // Fake data sending effect
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    Intent intent = getIntent();
                    setResult(RESULT_OK, intent);
                    intent.putExtra(NEW_RECIPE_ADDED, true);
                    intent.putExtra(STATE_RECIPE_NAME, recipeNameEditText.getText().toString());
                    intent.putExtra(STATE_DESCRIPTION, descriptionEditText.getText().toString());
                    intent.putExtra(STATE_TIME_HOUR, time.first);
                    intent.putExtra(STATE_TIME_MINUTES, time.second);
                    //intent.putExtra(STATE_WEEK_DAYS, weekDays);
                    // You must set confirmBack to false before calling finish() to avoid the confirmation dialog
                    confirmBack = false;
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start(); // You should delete this code and add yours
    }


    /**
     * Convert price to the correct format
     *
     * @param originPrice
     * @return
     */
    private String convertToPriceFormat(double originPrice) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        return  decimalFormat.format(originPrice)+" $";

    }



    // STEP CREATION

    private View createRecipePictureStep() {
        // This step view is generated programmatically
        mImgViewButton = new ImageView(this);
        mImgViewButton.setBackgroundResource(android.R.drawable.ic_menu_camera);
        mImgViewButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                switch (arg1.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        try {
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Couldn't load photo, error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        break;
                    }

                }
                verticalStepperForm.setActiveStepAsCompleted();
                return true;
            }
        });
        return mImgViewButton;
    }

    private View createCategoriesStep() {
        // This step view is generated by inflating a layout XML file
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        LinearLayout categoriesStepContent =
                (LinearLayout) inflater.inflate(R.layout.step_categories, null, false);
        categoriesTextView = (TextView)  categoriesStepContent.findViewById(R.id.categories);
        categoriesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Test
                if(categoriesDialog==null)
                {
                    setCategoriesSpinner();
                }
                categoriesDialog.show();
            }
        });
        return  categoriesStepContent;
    }

    private View createRecipeTitleStep() {
        // This step view is generated programmatically
        recipeNameEditText = new EditText(this);
        recipeNameEditText.setHint(R.string.form_hint_title);
        recipeNameEditText.setTextColor(Color.BLACK);
        recipeNameEditText.setSingleLine(true);
        recipeNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkTitleStep(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        recipeNameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (checkTitleStep(v.getText().toString())) {
                    verticalStepperForm.goToNextStep();
                }
                return false;
            }
        });
        return recipeNameEditText;
    }

    private View createRecipeDescriptionStep() {
        descriptionEditText = new EditText(this);
        descriptionEditText.setHint(R.string.form_hint_description);
        descriptionEditText.setTextColor(Color.BLACK);
        descriptionEditText.setSingleLine(true);
        descriptionEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                verticalStepperForm.goToNextStep();
                return false;
            }
        });
        return descriptionEditText;
    }

    private View createPriceStep() {
        // This step view is generated by inflating a layout XML file
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        LinearLayout priceStepContent =
                (LinearLayout) inflater.inflate(R.layout.step_price, null, false);
        priceTextView = (TextView)  priceStepContent.findViewById(R.id.price);
        priceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Test
                if(priceRecipeDialog==null)
                {
                    setPricePickers(MIN_PRICE_NUMBER,MAX_PRICE_NUMBER);
                }
                priceRecipeDialog.show();
            }
        });
        return  priceStepContent;
    }


    private View createPersonNumberStep() {
        // This step view is generated by inflating a layout XML file
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        LinearLayout numberStepContent =
                (LinearLayout) inflater.inflate(R.layout.step_number_person, null, false);
        nbPersonTextView = (TextView)  numberStepContent.findViewById(R.id.number_person);
        nbPersonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Test
                if(numberPersonDialog==null)
                {
                    setPersonNumberPicker(MIN_PERSON_NUMBER,MAX_PERSON_NUMBER);
                }
                numberPersonDialog.show();
            }
        });
        return  numberStepContent;

    }

    private View createRecipeTimeStep() {
        // This step view is generated by inflating a layout XML file
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        LinearLayout timeStepContent =
                (LinearLayout) inflater.inflate(R.layout.step_time_layout, null, false);
        timeTextView = (TextView) timeStepContent.findViewById(R.id.time);
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker.show();
            }
        });
        return timeStepContent;
    }

    private void setTimePicker(int hour, int minutes) {
        timePicker = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        setTime(hourOfDay, minute);
                    }
                }, hour, minutes, true);
    }

    private void setCategoriesSpinner() {
        categoriesSpinner = new Spinner(this);

        // load categories from database
        List<String> labels=mDBhelperRecipes.getAllCategoriesLabels();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, labels);

        // attaching data adapter to spinner
        categoriesSpinner.setAdapter(dataAdapter);

        categoriesSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                categoriesTextView.setText(""+adapterView.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }


        });

        AlertDialog.Builder builder  = new AlertDialog.Builder(this).setView(categoriesSpinner);
        builder.setTitle("Categories");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                verticalStepperForm.setActiveStepAsCompleted();
                dialog.dismiss();

            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                verticalStepperForm.setActiveStepAsUncompleted("titleError");
                dialog.cancel();
            }
        });

        categoriesDialog=builder.create();
    }

    private void setPricePickers( int minPrice , int maxPrice) {
        recipePrice=0;
        firstDigit=0;
        secondDigit=0;
        thirdDigit=0;
        fourthDigit=0;
        priceLinearLayout = new LinearLayout(this);
        priceLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

        // first picker
        firstNumberPicker = new NumberPicker(this);
        firstNumberPicker.setMinValue(minPrice); // restricted number to minimum value i.e 1
        firstNumberPicker.setMaxValue(maxPrice); // restricked number to maximum value i.e. 9
        firstNumberPicker.setWrapSelectorWheel(true);

        firstNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener()
        {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal)
            {
                firstDigit=newVal*10;
            }
        });


        // second picker
        secondNumberPicker = new NumberPicker(this);
        secondNumberPicker.setMinValue(minPrice); // restricted number to minimum value i.e 1
        secondNumberPicker.setMaxValue(maxPrice); // restricked number to maximum value i.e. 9
        secondNumberPicker.setWrapSelectorWheel(true);

        secondNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener()
        {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal)
            {
                //recipePrice=recipePrice-oldVal;
                secondDigit=newVal;

            }
        });


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(50, 50);
        params.gravity = Gravity.CENTER;

        LinearLayout.LayoutParams numPicerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        numPicerParams.weight = 1;

        LinearLayout.LayoutParams qPicerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        qPicerParams.weight = 1;

        priceLinearLayout.setLayoutParams(params);
        priceLinearLayout.addView(firstNumberPicker,numPicerParams);
        priceLinearLayout.addView(secondNumberPicker,qPicerParams);


        AlertDialog.Builder builder  = new AlertDialog.Builder(this).setView(priceLinearLayout);
        builder.setTitle("Price");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                recipePrice=firstDigit+secondDigit+thirdDigit+fourthDigit;
                priceTextView.setText(convertToPriceFormat(recipePrice));
                verticalStepperForm.setActiveStepAsCompleted();
                dialog.dismiss();

            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                verticalStepperForm.setActiveStepAsUncompleted("titleError");
                dialog.cancel();
            }
        });

        priceRecipeDialog=builder.create();
    }


    private void setPersonNumberPicker( int minPerson , int maxPerson) {
        numberPersonPicker = new NumberPicker(this);
        numberPersonPicker.setMinValue(minPerson); // restricted number to minimum value i.e 1
        numberPersonPicker.setMaxValue(maxPerson); // restricked number to maximum value i.e. 10
        numberPersonPicker.setWrapSelectorWheel(true);

        numberPersonPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener()
        {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal)
            {
                nbPersonTextView.setText(""+newVal);
            }
        });

        AlertDialog.Builder builder  = new AlertDialog.Builder(this).setView(numberPersonPicker);
        builder.setTitle("Person");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                verticalStepperForm.setActiveStepAsCompleted();
                dialog.dismiss();

            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                verticalStepperForm.setActiveStepAsUncompleted("titleError");
                dialog.cancel();
            }
        });

        numberPersonDialog=builder.create();
    }

    private boolean checkTitleStep(String title) {
        boolean titleIsCorrect = false;

        if (title.length() >= MIN_CHARACTERS_RECIPE_NAME) {
            titleIsCorrect = true;

            verticalStepperForm.setActiveStepAsCompleted();
            // Equivalent to: verticalStepperForm.setStepAsCompleted(TITLE_STEP_NUM);

        } else {
            String titleErrorString = getResources().getString(R.string.error_title_min_characters);
            String titleError = String.format(titleErrorString, MIN_CHARACTERS_RECIPE_NAME);

            verticalStepperForm.setActiveStepAsUncompleted(titleError);
            // Equivalent to: verticalStepperForm.setStepAsUncompleted(TITLE_STEP_NUM, titleError);

        }

        return titleIsCorrect;
    }

    private void setTime(int hour, int minutes) {
        time = new Pair<>(hour, minutes);
        String hourString = ((time.first > 9) ?
                String.valueOf(time.first) : ("0" + time.first));
        String minutesString = ((time.second > 9) ?
                String.valueOf(time.second) : ("0" + time.second));
        String time = hourString + ":" + minutesString;
        timeTextView.setText(time);
    }



    // CONFIRMATION DIALOG WHEN USER TRIES TO LEAVE WITHOUT SUBMITTING

    private void confirmBack() {
        if (confirmBack && verticalStepperForm.isAnyStepCompleted()) {
            BackConfirmationFragment backConfirmation = new BackConfirmationFragment();
            backConfirmation.setOnConfirmBack(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    confirmBack = true;
                }
            });
            backConfirmation.setOnNotConfirmBack(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    confirmBack = false;
                    finish();
                }
            });
            backConfirmation.show(getSupportFragmentManager(), null);
        } else {
            confirmBack = false;
            finish();
        }
    }

    private void dismissDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        if (numberPersonDialog != null && numberPersonDialog.isShowing()) {
            numberPersonDialog.dismiss();
        }

        if (categoriesDialog != null && categoriesDialog.isShowing()) {
            categoriesDialog.dismiss();
        }

        if (priceRecipeDialog != null && priceRecipeDialog.isShowing()) {
            priceRecipeDialog.dismiss();
        }

        priceRecipeDialog=null;
        categoriesDialog=null;
        numberPersonDialog=null;
        progressDialog = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home && confirmBack) {
            confirmBack();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        confirmBack();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissDialog();
    }



    // SAVING AND RESTORING THE STATE

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Saving title field
        if (recipeNameEditText != null) {
            savedInstanceState.putString(STATE_RECIPE_NAME, recipeNameEditText.getText().toString());
        }

        // Saving description field
        if (descriptionEditText != null) {
            savedInstanceState.putString(STATE_DESCRIPTION, descriptionEditText.getText().toString());
        }

        // Saving time field
        if (time != null) {
            savedInstanceState.putInt(STATE_TIME_HOUR, time.first);
            savedInstanceState.putInt(STATE_TIME_MINUTES, time.second);
        }

        // The call to super method must be at the end here
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        // Restoration of title field
        if (savedInstanceState.containsKey(STATE_RECIPE_NAME)) {
            String title = savedInstanceState.getString(STATE_RECIPE_NAME);
            recipeNameEditText.setText(title);
        }

        // Restoration of description field
        if (savedInstanceState.containsKey(STATE_DESCRIPTION)) {
            String description = savedInstanceState.getString(STATE_DESCRIPTION);
            descriptionEditText.setText(description);
        }

        // Restoration of time field
        if (savedInstanceState.containsKey(STATE_TIME_HOUR)
                && savedInstanceState.containsKey(STATE_TIME_MINUTES)) {
            int hour = savedInstanceState.getInt(STATE_TIME_HOUR);
            int minutes = savedInstanceState.getInt(STATE_TIME_MINUTES);
            time = new Pair<>(hour, minutes);
            setTime(hour, minutes);
            if (timePicker == null) {
                setTimePicker(hour, minutes);
            } else {
                timePicker.updateTime(hour, minutes);
            }
        }

        // The call to super method must be at the end here
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        try {
            switch (requestCode) {
                case CAMERA_PIC_REQUEST:
                    if (resultCode == RESULT_OK) {
                        try {
                            Bitmap photo = (Bitmap) data.getExtras().get("data");

                            mImgImageRecipe.setImageBitmap(photo);

                        } catch (Exception e) {
                            Toast.makeText(this, "Couldn't load photo, error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
        }
    }


}
