package com.shirleyhe.aitfinalproject.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ViewSwitcher;
import butterknife.BindView;
import butterknife.OnClick;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.ConceptModel;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

import com.shirleyhe.aitfinalproject.App;
import com.shirleyhe.aitfinalproject.R;
import com.shirleyhe.aitfinalproject.adapter.EbayPagerAdapter;
import com.shirleyhe.aitfinalproject.adapter.RecognizeConceptsAdapter;
import com.shirleyhe.aitfinalproject.fragment.ItemDetailsFragment;
//import com.shirleyhe.aitfinalproject.adapter.RecognizeConceptsAdapter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public final class RecognizeConceptsActivity extends BaseActivity {

    public static final int PICK_IMAGE = 100;
    private ArrayList<String> tags = new ArrayList<>();


    private String passKeyWord = "";

    // the list of results that were returned from the API
    @BindView(R.id.resultsList) RecyclerView resultsList;

    // the view where the image the user selected is displayed
    @BindView(R.id.image) ImageView imageView;

    // switches between the text prompting the user to hit the FAB, and the loading spinner
    @BindView(R.id.switcher) ViewSwitcher switcher;

    // the FAB that the user clicks to select an image
    @BindView(R.id.fab) View fab;

    @NonNull private final RecognizeConceptsAdapter adapter = new RecognizeConceptsAdapter();

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override protected void onStart() {
        super.onStart();

        resultsList.setLayoutManager(new LinearLayoutManager(this));
        resultsList.setAdapter(adapter);
    }

    @OnClick(R.id.fab)
    void pickImage() {
        //original
        //startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), PICK_IMAGE);
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
        // Now we will upload our image to the Clarifai API
        setBusy(true);

        // Make sure we don't show a list of old concepts while the image is being uploaded
        adapter.setData(Collections.<Concept>emptyList());

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
                setBusy(false);
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

                ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new EbayPagerAdapter(getSupportFragmentManager()));

        findItemDetailsFragment().navigateToStringUrl
                ("http://www.ebay.com/sch/i.html?_from=R40&_trksid=p2050601.m570.l1313.TR0.TRC0.H0.X"+passKeyWord+".TRS0&_nkw="+passKeyWord+"&_sacat=0");


            }

            private void showErrorSnackbar(@StringRes int errorString) {
                Snackbar.make(
                        root,
                        errorString,
                        Snackbar.LENGTH_INDEFINITE
                ).show();
            }
        }.execute();

        //do the view pager here
//        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
//        viewPager.setAdapter(new EbayPagerAdapter(getSupportFragmentManager()));
//
//        findItemDetailsFragment().navigateToStringUrl
//                ("http://www.ebay.com/sch/i.html?_from=R40&_trksid=p2050601.m570.l1313.TR0.TRC0.H0.X"+passKeyWord+".TRS0&_nkw="+passKeyWord+"&_sacat=0");


    }

    //find ItemDetailsFragment from Activity
    private ItemDetailsFragment findItemDetailsFragment() {
        return (ItemDetailsFragment) getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.pager + ":" + 0);
    }

    //pass keywordstring to itemdetailsfragment
    public String getPassKeyWord() {
        return passKeyWord;
    }


    @Override protected int layoutRes() { return R.layout.activity_recognize; }

    private void setBusy(final boolean busy) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                switcher.setDisplayedChild(busy ? 1 : 0);
                imageView.setVisibility(busy ? GONE : VISIBLE);
                fab.setEnabled(!busy);
            }
        });
    }

}
