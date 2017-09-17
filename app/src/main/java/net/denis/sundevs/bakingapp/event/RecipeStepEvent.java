package net.denis.sundevs.bakingapp.event;

/**
 * Created by moham on 10/09/17.
 */

public class RecipeStepEvent {

    private int selectedPosition;

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }
}
