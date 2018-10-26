package com.simurgh.prayertimes;

import android.view.View;
import android.widget.Toast;

import com.cleveroad.slidingtutorial.Direction;
import com.cleveroad.slidingtutorial.OnTutorialPageChangeListener;
import com.cleveroad.slidingtutorial.PageOptions;
import com.cleveroad.slidingtutorial.TransformItem;
import com.cleveroad.slidingtutorial.TutorialFragment;
import com.cleveroad.slidingtutorial.TutorialOptions;
import com.cleveroad.slidingtutorial.TutorialPageOptionsProvider;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

/**
 * Created by moshe on 13/07/2017.
 */

public class CustomTutorialFragment extends TutorialFragment implements OnTutorialPageChangeListener{

    private static final String TAG = "CustomTutorialFragment";
    private static final int TOTAL_PAGES = 7;
    private static final int ACTUAL_PAGES_COUNT = 3;

    private final View.OnClickListener mOnSkipClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getActivity(), "Skip button clicked", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected TutorialOptions provideTutorialOptions() {
        return null;
    }

    @Override
    public void onPageChanged(int position) {

    }

    final TutorialPageOptionsProvider tutorialPageOptionsProvider = new TutorialPageOptionsProvider() {
        @NonNull
        @Override
        public PageOptions provide(int position) {
            @LayoutRes int pageLayoutResId;
            TransformItem[] tutorialItems;
            switch (position) {
                case 0: {
                    pageLayoutResId = R.layout.intro_first;
                    tutorialItems = new TransformItem[]{
                            TransformItem.create(R.id.iv_main, Direction.LEFT_TO_RIGHT, 0.1f),
                            TransformItem.create(R.id.iv_first, Direction.RIGHT_TO_LEFT, 0.2f),
                            TransformItem.create(R.id.iv_second, Direction.RIGHT_TO_LEFT, 0.3f),
                            TransformItem.create(R.id.iv_fourth, Direction.RIGHT_TO_LEFT, 0.5f),
                            TransformItem.create(R.id.iv_fifth, Direction.RIGHT_TO_LEFT, 0.6f),
                            TransformItem.create(R.id.iv_sixth, Direction.RIGHT_TO_LEFT, 0.7f),
                            TransformItem.create(R.id.iv_seventh, Direction.RIGHT_TO_LEFT, 0.8f),
                            TransformItem.create(R.id.iv_eigth, Direction.RIGHT_TO_LEFT, 0.9f)
                    };
                    break;
                }
                case 1: {
                    pageLayoutResId = R.layout.intro_second;
                    tutorialItems = new TransformItem[]{
                            TransformItem.create(R.id.iv_main, Direction.LEFT_TO_RIGHT, 0.2f),
                            TransformItem.create(R.id.iv_second, Direction.RIGHT_TO_LEFT, 0.05f),
                            TransformItem.create(R.id.iv_third, Direction.RIGHT_TO_LEFT, 0.07f)
                    };
                    break;
                }
                case 2: {
                    pageLayoutResId = R.layout.intro_third;
                    tutorialItems = new TransformItem[]{
                            TransformItem.create(R.id.iv_main, Direction.LEFT_TO_RIGHT, 0.2f),
                            TransformItem.create(R.id.iv_second, Direction.RIGHT_TO_LEFT, 0.05f),
                            TransformItem.create(R.id.iv_third, Direction.RIGHT_TO_LEFT, 0.07f)
                    };
                    break;
                }
                case 3: {
                    pageLayoutResId = R.layout.intro_fourth;
                    tutorialItems = new TransformItem[]{
                            TransformItem.create(R.id.iv_main, Direction.LEFT_TO_RIGHT, 0.2f),
                            TransformItem.create(R.id.iv_second, Direction.RIGHT_TO_LEFT, 0.05f),
                            TransformItem.create(R.id.iv_third, Direction.RIGHT_TO_LEFT, 0.07f)
                    };
                    break;
                }
                case 4: {
                    pageLayoutResId = R.layout.intro_fifth;
                    tutorialItems = new TransformItem[]{
                            TransformItem.create(R.id.iv_main, Direction.LEFT_TO_RIGHT, 0.2f),
                            TransformItem.create(R.id.iv_second, Direction.RIGHT_TO_LEFT, 0.05f),
                            TransformItem.create(R.id.iv_third, Direction.RIGHT_TO_LEFT, 0.07f)
                    };
                    break;
                }
                case 5: {
                    pageLayoutResId = R.layout.intro_sixth;
                    tutorialItems = new TransformItem[]{
                            TransformItem.create(R.id.iv_main, Direction.LEFT_TO_RIGHT, 0.2f),
                            TransformItem.create(R.id.iv_second, Direction.RIGHT_TO_LEFT, 0.05f),
                            TransformItem.create(R.id.iv_third, Direction.RIGHT_TO_LEFT, 0.07f)
                    };
                    break;
                }
                case 6: {
                    pageLayoutResId = R.layout.intro_seventh;
                    tutorialItems = new TransformItem[]{
                            TransformItem.create(R.id.iv_main, Direction.LEFT_TO_RIGHT, 0.2f)
                    };
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Unknown position: " + position);
                }
            }

            return PageOptions.create(pageLayoutResId, position, tutorialItems);
        }
    };

}
