package fr.wayofcode.studyandcook.activities;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import fr.wayofcode.studyandcook.utils.StorageHelper;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.fragments.BackConfirmationFragment;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;
import fr.wayofcode.studyandcook.R;
import fr.wayofcode.studyandcook.adapters.DirectionAdapter;
import fr.wayofcode.studyandcook.databases.DbHelperRecipes;
import fr.wayofcode.studyandcook.model.Direction;

public class NewRecipeActivity extends AppCompatActivity implements VerticalStepperForm {

    // Constants
    private static final int CAMERA_PIC_REQUEST = 22;
    public static final String NEW_RECIPE_ADDED = "new_recipe_added";

    // Constants about the steps/fields of the form
    private static final int RECIPE_PICTURE_STEP_NUM = 0;
    private static final int RECIPE_TITLE_STEP_NUM = 1;
    private static final int TIME_STEP_NUM = 2;
    private static final int SUMMARY_DESCRIPTION_STEP_NUM = 3;
    private static final int NUMBER_PERSON_STEP_NUM = 4;
    private static final int RECIPE_CATEGORY_STEP_NUM = 5;
    private static final int PRICE_STEP_NUM = 6;
    private static final int INGREDIENT_DESCRIPTION_STEP_NUM = 7;
    private static final int DIRECTIONS_DESCRIPTION_STEP_NUM = 8;

    // Database
    DbHelperRecipes mDBhelperRecipes;

    // Recipe picture step
    private ImageView mImgImageRecipe;
    private Button addPictureBtn;
    private Bitmap pictureBitmap;
    private ByteArrayOutputStream bytearrayoutputstream;
    public static final String STATE_RECIPE_PICTURE = "picture";

    // Recipe title step
    private EditText recipeNameEditText;
    private TextInputLayout titleTxtInputLayout;
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
    private static final int DEFAULT_TIME_HOUR=1;
    private static final int DEFAULT_TIME_MINUTE=30;
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
    private TextView priceTextView;
    private LinearLayout priceLinearLayout;
    private AlertDialog priceRecipeDialog ;
    private NumberPicker firstNumberPicker;
    private NumberPicker secondNumberPicker;
    private static final int MIN_PRICE_NUMBER = 0;
    private static final int MAX_PRICE_NUMBER = 9;
    public static final String STATE_PRICE = "price";

    // Recipe Ingredient step
    private EditText editTxtIngredient;
    private Button btnAdd;
    private String ingredients;
    private Button addElementBtn;
    private TextView ingredientTextView;
    private AlertDialog ingredientRecipeDialog ;
    private ListView listViewIngredient;
    private LinearLayout ingredientLinearLayout;
    private ArrayAdapter<String> adapterIngredient;
    private ArrayList<String> mainListIngredient;
    private ArrayList<String> tempListIngredient;
    public static final String STATE_INGREDIENT = "ingredient";

    // Summary description step
    private EditText descriptionEditText;
    private static final int MIN_CHARACTERS_RECIPE_DESCRIPTION = 5;
    public static final String STATE_DESCRIPTION = "description";

    // Direction step
    private EditText directionEditTxt;
    private Button directionAddBtn;
    private String directions;
    private TextView directionTextView;
    private int directionCounter;
    private Button addNewDirectionBtn;
    private AlertDialog directionRecipeDialog ;
    private ListView directionListView;
    private LinearLayout directionLinearLayout;
    private DirectionAdapter adapterDirection;
    private ArrayList<Direction> listDirection;
    public static final String STATE_DIRECTION = "direction";

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

        // Initialize the database
        try
        {
        mDBhelperRecipes = new DbHelperRecipes(this);

            if(mDBhelperRecipes.checkDataBase()) {
                mDBhelperRecipes.openDataBase();
            }
            else
            {
                mDBhelperRecipes.createDataBase();
            }

        }catch(SQLException sqle){
        throw new Error("Unable to open database : ");
         }
        catch(IOException ioe){
            throw new Error("Unable to create database");
        }

        // Set toolbar as actionbar and up navigation button and set toolbar title to null
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_logo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set Recipe image View
        this.mImgImageRecipe = (ImageView) this.findViewById(R.id.image_recipe);

        // Time step vars
        time = new Pair<>(DEFAULT_TIME_HOUR, DEFAULT_TIME_MINUTE);
        setTimePicker(DEFAULT_TIME_HOUR, DEFAULT_TIME_MINUTE);

        // Number person step vars
        setPersonNumberPicker(MIN_PERSON_NUMBER,MAX_PERSON_NUMBER);

        // Categories step vars
        setCategoriesSpinner();

        // Price step vars
        setPricePickers(MIN_PRICE_NUMBER,MAX_PRICE_NUMBER);

        // Ingredient step vars
        setIngredientComponent();

        // Direction step vars
        setDirectionComponent();

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
            case RECIPE_CATEGORY_STEP_NUM:
                view = createCategoriesStep();
                break;
            case PRICE_STEP_NUM:
                view = createPriceStep();
                break;
            case INGREDIENT_DESCRIPTION_STEP_NUM:
                view = createIngredientStep();
                break;
            case DIRECTIONS_DESCRIPTION_STEP_NUM:
                view = createDirectionStep();
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
                // When this step is open, we check that the summary is correct
                checkSummaryStep(descriptionEditText.getText().toString());
                break;
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
                    intent.putExtra(STATE_INGREDIENT,ingredients);
                    intent.putExtra(STATE_RECIPE_CATEGORIES, categoriesTextView.getText().toString());
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
        addPictureBtn= new Button(this);

        addPictureBtn.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_camera,0,0,0);
        addPictureBtn.setCompoundDrawablePadding(15);
        addPictureBtn.setBackgroundResource(R.drawable.tags_rounded_corners);
        addPictureBtn.setText("Add picture ");
        addPictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        try {
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Couldn't load photo, error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                verticalStepperForm.setActiveStepAsCompleted();

            }
        });
        return addPictureBtn;
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

        // EditText
        // Instantiate EditText view which will be held inside of
        // TextInputLayout
        recipeNameEditText = new EditText(this);
        // Add an ID to it
        recipeNameEditText.setId(View.generateViewId());
        // Get the Hint text for EditText field which will be presented to the
        // user in the TextInputLayout
        recipeNameEditText.setHint(R.string.form_hint_title);
        // Set color of the hint text inside the EditText field
        recipeNameEditText.setHintTextColor(Color.BLACK);
        // Set the font size of the text that the user will enter
        recipeNameEditText.setTextSize(16);

        recipeNameEditText.setBackgroundColor(Color.GRAY);
        // Set the color of the text inside the EditText field
        recipeNameEditText.setTextColor(Color.BLACK);
        // Define layout params for the EditTExt field
        RelativeLayout.LayoutParams editTextParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        // Set editText layout parameters to the editText field
        recipeNameEditText.setLayoutParams(editTextParams);

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

        /*
         * Next, you do the same thing for the TextInputLayout (instantiate,
         * generate and set ID, set layoutParams, set layoutParamt for
         * TextInputLayout
         */

        // TextInputLayout
        titleTxtInputLayout = new TextInputLayout(this);
        titleTxtInputLayout.setId(View.generateViewId());
        RelativeLayout.LayoutParams textInputLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        titleTxtInputLayout.setLayoutParams(textInputLayoutParams);

        // Then you add editText into a textInputLayout
        titleTxtInputLayout.addView(recipeNameEditText, editTextParams);

        return titleTxtInputLayout;
    }

    private View createRecipeDescriptionStep() {
        descriptionEditText = new EditText(this);
        descriptionEditText.setHint(R.string.form_hint_description);
        descriptionEditText.setHintTextColor(Color.BLACK);
        descriptionEditText.setBackgroundColor(Color.GRAY);
        descriptionEditText.setTextColor(Color.BLACK);
        descriptionEditText.setSingleLine(true);
        descriptionEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                verticalStepperForm.goToNextStep();
                return false;
            }
        });

        descriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkSummaryStep(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        descriptionEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (checkSummaryStep(v.getText().toString())) {
                    verticalStepperForm.goToNextStep();
                }
                return false;
            }
        });



        return descriptionEditText;
    }

    private View createDirectionStep() {
        // This step view is generated by inflating a layout XML file
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        LinearLayout directionStepContent =
                (LinearLayout) inflater.inflate(R.layout.step_direction, null, false);
        directionListView = (ListView)  directionStepContent.findViewById(R.id.list_direction);
        adapterDirection=new DirectionAdapter(this,listDirection);
        directionListView.setAdapter(adapterDirection);

        directionAddBtn = (Button) directionStepContent.findViewById(R.id.directionAddBtn);
        directionAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Test
                if(directionRecipeDialog==null) {
                    setIngredientComponent();
                }
                directionRecipeDialog.show();
            }
        });
        return  directionStepContent;
    }

    private  View createIngredientStep() {

        // This step view is generated by inflating a layout XML file
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        LinearLayout ingredientStepContent =
                (LinearLayout) inflater.inflate(R.layout.step_ingredient, null, false);
        ingredientTextView = (TextView)  ingredientStepContent.findViewById(R.id.ingredients_list);

        btnAdd = (Button) ingredientStepContent.findViewById(R.id.addBtn);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Test
                if(ingredientRecipeDialog==null) {
                    setIngredientComponent();
                }
                ingredientRecipeDialog.show();
            }
        });
        return  ingredientStepContent;
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

    private void setDirectionComponent() {

        directionCounter=0;
        directionEditTxt = new EditText(this);

        listDirection= new ArrayList<>();

        adapterDirection=new DirectionAdapter(this, listDirection);

        addNewDirectionBtn=new Button(this);
        addNewDirectionBtn.setText("ADD NEW DIRECTION");
        addNewDirectionBtn.setTextColor(Color.WHITE);
        addNewDirectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this line adds the data of your EditText and puts in your array
                if(directionEditTxt.getText().toString() != null && directionEditTxt.getText().toString().trim() != ""  ) {

                    Direction direction = new Direction(Integer.toString(directionCounter), directionEditTxt.getText().toString());
                    listDirection.add(direction);
                    directionEditTxt.setText("");
                    // next thing you have to do is check if your adapter has changed
                    adapterDirection.add(direction);
                    adapterDirection.notifyDataSetChanged();
                    ;
                    directionCounter++;
                }
            }
        });



    }


    private void setIngredientComponent() {
        editTxtIngredient = new EditText(this);
        editTxtIngredient.setText("");

        mainListIngredient = new ArrayList<>();
        tempListIngredient = new ArrayList<>();

        addElementBtn=new Button(this);
        addElementBtn.setText("ADD NEW INGREDIENT");
        addElementBtn.setTextColor(Color.WHITE);
        addElementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( !editTxtIngredient.getText().toString().trim().equals("") ) {
                    // this line adds the data of your EditText and puts in your array
                    tempListIngredient.add("+ " + editTxtIngredient.getText().toString());
                    editTxtIngredient.setText("");
                    // next thing you have to do is check if your adapter has changed
                    //adapterIngredient.add(editTxtIngredient.getText().toString());
                    adapterIngredient.notifyDataSetChanged();
                }
            }
        });

        listViewIngredient= new ListView(this);

        // Adapter: You need three parameters 'the context, id of the layout (it will be where the data is shown),
        // and the array that contains the data
        adapterIngredient = new ArrayAdapter<>(getApplicationContext(), R.layout.list_ingredient_item,
            tempListIngredient);

        listViewIngredient.setAdapter(adapterIngredient);

        //create a layout
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        ingredientLinearLayout = new LinearLayout(this);
        ingredientLinearLayout.setOrientation(LinearLayout.VERTICAL);
        ingredientLinearLayout.setLayoutParams(params);

        ingredientLinearLayout.addView(editTxtIngredient);
        ingredientLinearLayout.addView(addElementBtn);
        ingredientLinearLayout.addView(listViewIngredient);

        AlertDialog.Builder builder  = new AlertDialog.Builder(this).setView(ingredientLinearLayout);
        builder.setTitle("Ingredients");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // this line adds the data of your EditText and puts in your array
                if(!tempListIngredient.isEmpty() )
                {
                    if(tempListIngredient.size() >1) {
                        for (String ing : tempListIngredient) {

                            if(!mainListIngredient.contains(ing))
                            {
                                mainListIngredient.add(ing);
                            }

                        }

                    }
                    else
                    {
                        if(!mainListIngredient.contains(tempListIngredient.get(0)))
                        {
                            mainListIngredient.add(tempListIngredient.get(0));
                        }
                    }

                    int i = 0;
                    StringBuilder concatIng = new StringBuilder();

                    for(String ingr : mainListIngredient) {
                        concatIng.append(ingr);

                        if (i != (mainListIngredient.size() - 1)) {
                            concatIng.append("\n");
                        }
                        i++;

                    }

                    ingredients = concatIng.toString();
                    ingredientTextView.setText(ingredients);
                }

                tempListIngredient.clear();
                verticalStepperForm.setActiveStepAsCompleted();

                dialog.dismiss();

            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //verticalStepperForm.setActiveStepAsUncompleted("titleError");
                tempListIngredient.clear();
                dialog.cancel();
            }
        });

        ingredientRecipeDialog=builder.create();
    }

    private void setCategoriesSpinner() {
        categoriesSpinner = new Spinner(this);

        // load categories from database
        List<String> labels=mDBhelperRecipes.getAllCategoriesLabels();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, labels);

        // attaching data adapter to spinner
        categoriesSpinner.setAdapter(dataAdapter);
        categoriesSpinner.setPadding(50,10,15,10);
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

        //create a layout
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout categoriesLinearLayout= new LinearLayout(this);
        categoriesLinearLayout.setOrientation(LinearLayout.VERTICAL);
        categoriesLinearLayout.setLayoutParams(params);

        categoriesLinearLayout.addView(categoriesSpinner);


        AlertDialog.Builder builder  = new AlertDialog.Builder(this).setView(categoriesLinearLayout);
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
                recipePrice=firstDigit+secondDigit;
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

    private boolean checkSummaryStep(String summary) {
        boolean summaryIsCorrect = false;

        if (summary.length() >= MIN_CHARACTERS_RECIPE_DESCRIPTION) {
            summaryIsCorrect = true;

            verticalStepperForm.setActiveStepAsCompleted();
            ;

        } else {
            String titleErrorString = getResources().getString(R.string.error_title_min_characters);
            String titleError = String.format(titleErrorString, MIN_CHARACTERS_RECIPE_DESCRIPTION);

            verticalStepperForm.setActiveStepAsUncompleted(titleError);


        }

        return summaryIsCorrect;
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

    private void saveCameraPicture( Bitmap picture , String recipeTitle) {

        File file;
        FileOutputStream fileoutputstream;

        String title=recipeTitle.replace(' ','_').toLowerCase();

        // compress picture to PNG format
        picture.compress(Bitmap.CompressFormat.PNG, 60, bytearrayoutputstream);

        try

        {
            // Test SD card is present
            if (StorageHelper.isExternalStorageReadableAndWritable()) {

                file = new File(
                    Environment.getExternalStorageDirectory() + "/" + title + ".png");


            } else {
                file = new File(
                    getFilesDir() + "/" + title + ".png");
            }

            // delete existing file
            if (file.exists())
                file.delete();

            // create new filz
            file.createNewFile();

            fileoutputstream = new FileOutputStream(file);

            fileoutputstream.write(bytearrayoutputstream.toByteArray());

            fileoutputstream.close();
        }
        catch (Exception e)

        {

            e.printStackTrace();

        }



    }





    private void dismissDialog() {


        if (ingredientRecipeDialog != null && ingredientRecipeDialog.isShowing()) {
            ingredientRecipeDialog.dismiss();
        }

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

        ingredientRecipeDialog=null;
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
