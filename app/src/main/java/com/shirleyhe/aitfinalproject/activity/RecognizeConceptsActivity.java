package com.shirleyhe.aitfinalproject.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.ConceptModel;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

import com.google.firebase.auth.FirebaseAuth;
import com.shirleyhe.aitfinalproject.App;
import com.shirleyhe.aitfinalproject.R;
import com.shirleyhe.aitfinalproject.adapter.EbayPagerAdapter;

import com.shirleyhe.aitfinalproject.fragment.ItemDetailsFragment;
//import com.shirleyhe.aitfinalproject.adapter.RecognizeConceptsAdapter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public final class RecognizeConceptsActivity extends AppCompatActivity {

    public static final int PICK_IMAGE = 100;
    private ArrayList<String> tags = new ArrayList<>();

    private String passKeyWord = "";

    private ViewPager viewPager;

    private CoordinatorLayout layoutContent;
    private DrawerLayout drawerLayout;


    @BindView(R.id.framelayout)
    FrameLayout frameLayout;

    @BindView(R.id.btnLogout)
    Button btnLogout;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize);

        ButterKnife.bind(this);

        layoutContent = (CoordinatorLayout) findViewById(
                R.id.layoutContent);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        switch (menuItem.getItemId()) {
                            case R.id.action_profile:
                                //Let user edit their profile
                                drawerLayout.closeDrawer(GravityCompat.START);
                                break;
                            case R.id.action_about:
                                showSnackBarMessage(getString(R.string.txt_about));
                                drawerLayout.closeDrawer(GravityCompat.START);
                                break;
                            case R.id.action_help:
                                showSnackBarMessage(getString(R.string.txt_help));
                                drawerLayout.closeDrawer(GravityCompat.START);
                                break;
                        }

                        return false;
                    }
                });

        setUpToolBar();


        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new EbayPagerAdapter(getSupportFragmentManager()));


    }

    private void setUpToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    private void showSnackBarMessage(String message) {
        Snackbar.make(layoutContent,
                message,
                Snackbar.LENGTH_LONG
        ).setAction(R.string.action_hide, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //...
            }
        }).show();
    }

    @OnClick(R.id.btnLogout)
    void userLogout(){
        FirebaseAuth.getInstance().signOut();
    }


    @OnClick(R.id.fab)
    public void pickImage() {
        Toast.makeText(RecognizeConceptsActivity.this, "fab pressed", Toast.LENGTH_LONG).show();
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), PICK_IMAGE);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch(requestCode) {
            case PICK_IMAGE:
                //original
                //final byte[] imageBytes = ClarifaiUtil.retrieveSelectedImage(this, data);

                //new - test!!
                //get bitmap
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                //convert to byte
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                final byte[] imageBytes = stream.toByteArray();
                if (imageBytes != null) {
                    onImagePicked(imageBytes);
                }
                break;

        }
    }

    private void onImagePicked(@NonNull final byte[] imageBytes) {

//        // Make sure we don't show a list of old concepts while the image is being uploaded
//        adapter.setData(Collections.<Concept>emptyList());

        new AsyncTask<Void, Void, ClarifaiResponse<List<ClarifaiOutput<Concept>>>>() {
            @Override protected ClarifaiResponse<List<ClarifaiOutput<Concept>>> doInBackground(Void... params) {
                // The default Clarifai model that identifies concepts in images
                final ConceptModel generalModel = App.get().clarifaiClient().getDefaultModels().generalModel();

                // Use this model to predict, with the image that the user just selected as the input
                return generalModel.predict()
                        .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(imageBytes)))
                        .executeSync();
            }

            @Override protected void onPostExecute(ClarifaiResponse<List<ClarifaiOutput<Concept>>> response) {
                if (!response.isSuccessful()) {
                    showErrorSnackbar(R.string.error_while_contacting_api);
                    return;
                }
                final List<ClarifaiOutput<Concept>> predictions = response.get();
                if (predictions.isEmpty()) {
                    showErrorSnackbar(R.string.no_results_from_api);
                    return;
                }

                //GETTIN TAGS HERE INSTEAD
                final List<Concept> predictedTags = predictions.get(0).data();
                for (int i = 0; i < predictedTags.size(); i++) {
                    tags.add(predictedTags.get(i).name());

                }
                passKeyWord = tags.get(0);

                String a = passKeyWord;

                viewPager.setCurrentItem(1);



                //making new bundle to pass passkeyword instead
//                Bundle bundle = new Bundle();
//                bundle.putString("passKeyWord", passKeyWord);
//
//                ItemDetailsFragment itemDetailsFragment = new ItemDetailsFragment();
//                itemDetailsFragment.setArguments(bundle);


                //passKeyWord = predictedTags.get(0).name();
               // adapter.setData(predictions.get(0).data());
            }

            private void showErrorSnackbar(@StringRes int errorString) {
                Snackbar.make(
                        frameLayout,
                        errorString,
                        Snackbar.LENGTH_INDEFINITE
                ).show();
            }
        }.execute();

    }

    //pass keywordstring to itemdetailsfragment
    public String getPassKeyWord() {
        return passKeyWord;
    }



}
