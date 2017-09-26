package net.denis.sundevs.bakingapp.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import net.denis.sundevs.bakingapp.App;
import net.denis.sundevs.bakingapp.R;
import net.denis.sundevs.bakingapp.event.RecipeStepEvent;
import net.denis.sundevs.bakingapp.model.Ingredients;
import net.denis.sundevs.bakingapp.model.Steps;
import net.denis.sundevs.bakingapp.view.adapter.RecipeStepsAdapter;
import net.denis.sundevs.bakingapp.view.listener.RecipeStepOnClickListener;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;
import static net.denis.sundevs.bakingapp.util.Constant.Data.EXTRA_INGREDIENTS;
import static net.denis.sundevs.bakingapp.util.Constant.Data.EXTRA_STEP;
import static net.denis.sundevs.bakingapp.util.Constant.Data.EXTRA_STEPS;
import static net.denis.sundevs.bakingapp.util.Constant.Data.EXTRA_STEP_FIRST;
import static net.denis.sundevs.bakingapp.util.Constant.Data.EXTRA_STEP_LAST;
import static net.denis.sundevs.bakingapp.util.Constant.Data.EXTRA_STEP_NUMBER;


/**
 * Created by moham on 10/09/17.
 */

public class RecipeDetailFragment extends Fragment implements RecipeStepOnClickListener {
    @BindView(R.id.detail_ingredients)
    TextView mDetailIngredients;

    @BindView(R.id.detail_steps)
    RecyclerView mDetailSteps;

    private List<Ingredients> mRecipeIngredients;
    private List<Steps> mRecipeSteps = new ArrayList<>();

    public RecipeDetailFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        initView(rootView);

        Bundle bundle = getArguments();
        String strIngredients = bundle.getString(EXTRA_INGREDIENTS);
        mRecipeIngredients = Arrays.asList(App.getInstance().getGson().fromJson(strIngredients, Ingredients[].class));

        String strSteps = bundle.getString(EXTRA_STEPS);
        mRecipeSteps = Arrays.asList(App.getInstance().getGson().fromJson(strSteps, Steps[].class));

        String strIngredient = "";
        for (Ingredients ingredient : mRecipeIngredients) {
            DecimalFormat format = new DecimalFormat("#.##");

            strIngredient += "- " + format.format(ingredient.getQuantity())
                    + " " + ingredient.getMeasure() + " of " + ingredient.getIngredient() + ".";
            strIngredient += "\n";
        }

        mDetailIngredients.setText(strIngredient);

        LinearLayoutManager recipeStepLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mDetailSteps.setLayoutManager(recipeStepLayoutManager);

        RecipeStepsAdapter recipeStepsAdapter = new RecipeStepsAdapter(this);
        mDetailSteps.setAdapter(recipeStepsAdapter);
        recipeStepsAdapter.setDataAdapter(mRecipeSteps);

        ViewCompat.setNestedScrollingEnabled(mDetailSteps, false);

        return rootView;
    }

    private void initView(View rootView) {
        ButterKnife.bind(this, rootView);
    }

    @Override
    public void onStepSelected(int selectedPosition) {
        EventBus eventBus = App.getInstance().getEventBus();
        RecipeStepEvent event = new RecipeStepEvent();
        event.setSelectedPosition(selectedPosition);
        eventBus.post(event);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(TAG, "state");
//        mRecipeSteps = mDetailSteps.onSaveInstanceState();
//         outState.putParcelable(EXTRA_STEPS, mRecipeSteps);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}