package byu.cs240.familymapclient.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import byu.cs240.familymapclient.R;
import byu.cs240.familymapclient.model.Settings;

public class SettingsActivity extends AppCompatActivity {
    private final static String TAG = "SettingsActivity";

    private Settings settings;

    private ImageView upButton;
    private Switch lifeStorySwitch;
    private Switch familyTreeSwitch;
    private Switch spouseSwitch;
    private Switch fatherSwitch;
    private Switch motherSwitch;
    private Switch maleSwitch;
    private Switch femaleSwitch;
    private LinearLayout logoutLinearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = Settings.getInstance();

        upButton = (ImageView)findViewById(R.id.settings_up_icon);
        upButton.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_arrow_left).colorRes(R.color.bg_color).sizeDp(20));
        upButton.setOnClickListener(upBtnClickListiner);

        lifeStorySwitch = (Switch)findViewById(R.id.life_story_switch);
        lifeStorySwitch.setOnClickListener(switchClickListener);

        familyTreeSwitch = (Switch)findViewById(R.id.family_tree_switch);
        familyTreeSwitch.setOnClickListener(switchClickListener);

        spouseSwitch = (Switch)findViewById(R.id.spouse_switch);
        spouseSwitch.setOnClickListener(switchClickListener);

        fatherSwitch = (Switch)findViewById(R.id.father_switch);
        fatherSwitch.setOnClickListener(switchClickListener);

        motherSwitch = (Switch)findViewById(R.id.mother_switch);
        motherSwitch.setOnClickListener(switchClickListener);

        maleSwitch = (Switch)findViewById(R.id.male_switch);
        maleSwitch.setOnClickListener(switchClickListener);

        femaleSwitch = (Switch)findViewById(R.id.female_switch);
        femaleSwitch.setOnClickListener(switchClickListener);

        onLoadSetSwitchChecked();

        logoutLinearLayout = (LinearLayout)findViewById(R.id.logout_box);
        logoutLinearLayout.setOnClickListener(logoutClickListener);
    }

    private View.OnClickListener switchClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            Log.i(TAG, Integer.toString(view.getId()));

            switch(view.getId()) {
                case R.id.life_story_switch: settings.toggleDrawLifeLines(); break;
                case R.id.family_tree_switch: settings.toggleDrawFamilyLines(); break;
                case R.id.spouse_switch: settings.toggleDrawSpouseLine(); break;
                case R.id.father_switch: settings.toggleDisplayFatherEvents(); break;
                case R.id.mother_switch: settings.toggleDisplayMotherEvents(); break;
                case R.id.male_switch: settings.toggleDisplayMaleEvents(); break;
                case R.id.female_switch: settings.toggleDisplayFemaleLines(); break;
                default: Log.i(TAG, "ID doesn't match a switch");
            }
            Log.i(TAG, settings.toString());
        }
    };

    private void onLoadSetSwitchChecked() {
        lifeStorySwitch.setChecked(settings.drawLifeLines());
        familyTreeSwitch.setChecked(settings.drawFamilyLines());
        spouseSwitch.setChecked(settings.drawSpouseLines());
        fatherSwitch.setChecked(settings.displayFatherSide());
        motherSwitch.setChecked(settings.displayMotherSide());
        maleSwitch.setChecked(settings.displayMaleEvents());
        femaleSwitch.setChecked(settings.displayFemaleEvents());
    }


    private View.OnClickListener logoutClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            Log.i(TAG, "logout");
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    };

    private View.OnClickListener upBtnClickListiner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };
}