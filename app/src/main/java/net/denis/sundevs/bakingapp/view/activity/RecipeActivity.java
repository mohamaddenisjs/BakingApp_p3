package net.denis.sundevs.bakingapp.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.ButterKnife;
import net.denis.sundevs.bakingapp.App;
import net.denis.sundevs.bakingapp.R;
import net.denis.sundevs.bakingapp.event.RecipeStepEvent;
import net.denis.sundevs.bakingapp.model.Ingredients;
import net.denis.sundevs.bakingapp.model.Recipe;
import net.denis.sundevs.bakingapp.model.Steps;
import net.denis.sundevs.bakingapp.view.fragment.RecipeDetailFragment;
import net.denis.sundevs.bakingapp.view.fragment.RecipeStepDetailFragment;

import static net.denis.sundevs.bakingapp.util.TAG.Data.EXTRA_INGREDIENTS;
import static net.denis.sundevs.bakingapp.util.TAG.Data.EXTRA_IS_RECIPE_MENU;
import static net.denis.sundevs.bakingapp.util.TAG.Data.EXTRA_RECIPE;
import static net.denis.sundevs.bakingapp.util.TAG.Data.EXTRA_STEP;
import static net.denis.sundevs.bakingapp.util.TAG.Data.EXTRA_STEPS;
import static net.denis.sundevs.bakingapp.util.TAG.Data.EXTRA_STEP_FIRST;
import static net.denis.sundevs.bakingapp.util.TAG.Data.EXTRA_STEP_LAST;
import static net.denis.sundevs.bakingapp.util.TAG.Data.EXTRA_STEP_NUMBER;
import static net.denis.sundevs.bakingapp.util.TAG.Data.EXTRA_STEP_POSITION;

/**
 * Created by moham on 10/09/17.
 */

public class RecipeActivity extends AppCompatActivity {
    private Recipe mDetailRecipe;
    private FragmentManager fragmentManager;
    private boolean mIsRecipeMenu = false;
    private EventBus eventBus;
    private List<Ingredients> mRecipeIngredients;
    private List<Steps> mRecipeSteps;
    private int mSelectedPosition = -1;
    private boolean isTwoPanel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        eventBus = App.getInstance().getEventBus();

        mDetailRecipe = App.getInstance().getGson().fromJson(this.getIntent().getExtras().getString(EXTRA_RECIPE), Recipe.class);
        mRecipeIngredients = mDetailRecipe.getIngredients();
        mRecipeSteps = mDetailRecipe.getSteps();

        initView();

        isTwoPanel = findViewById(R.id.recipe_fragment_menu) != null;

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.recipe_fragment, new Fragment()).commit();

        showRecipeMenu();

        if (savedInstanceState != null) {
            mIsRecipeMenu = savedInstanceState.getBoolean(EXTRA_IS_RECIPE_MENU);
            mSelectedPosition = savedInstanceState.getInt(EXTRA_STEP_POSITION);
            if (mIsRecipeMenu) {
                showRecipeMenu();
            } else {
                showRecipeStepFragment(mSelectedPosition);
            }
        }
    }

    private void showRecipeMenu() {
        setTitle(mDetailRecipe.getName());

        RecipeDetailFragment fragment = new RecipeDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_INGREDIENTS, App.getInstance().getGson().toJson(mRecipeIngredients));
        bundle.putString(EXTRA_STEPS, App.getInstance().getGson().toJson(mRecipeSteps));
        fragment.setArguments(bundle);

        if (isTwoPanel)
            fragmentManager.beginTransaction().replace(R.id.recipe_fragment_menu, fragment).commit();
        else fragmentManager.beginTransaction().replace(R.id.recipe_fragment, fragment).commit();
        mIsRecipeMenu = true;
    }

    private void initView() {
        ButterKnife.bind(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        eventBus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        eventBus.unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showRecipeStep(RecipeStepEvent event) {
        if (mSelectedPosition != event.getSelectedPosition()) {
            mSelectedPosition = event.getSelectedPosition();
            showRecipeStepFragment(mSelectedPosition);
        }
    }

    private void showRecipeStepFragment(int stepNumber) {
        Steps step = mRecipeSteps.get(stepNumber);
        setTitle(mDetailRecipe.getName() + " - " + step.getShortDescription());

        RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_STEP, App.getInstance().getGson().toJson(step));
        bundle.putInt(EXTRA_STEP_NUMBER, stepNumber);
        bundle.putBoolean(EXTRA_STEP_FIRST, stepNumber == 0);
        bundle.putBoolean(EXTRA_STEP_LAST, stepNumber == (mRecipeSteps.size() - 1));
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.recipe_fragment, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
        mIsRecipeMenu = false;
    }

    @Override
    public void onBackPressed() {
        if (isTwoPanel) {
            super.onBackPressed();
            return;
        }
        Fragment fragmentById = fragmentManager.findFragmentById(R.id.recipe_fragment);
        if (fragmentById instanceof RecipeStepDetailFragment) {
            showRecipeMenu();
            mSelectedPosition = -1;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_STEP_POSITION, mSelectedPosition);
        outState.putBoolean(EXTRA_IS_RECIPE_MENU, mIsRecipeMenu);
    }
}