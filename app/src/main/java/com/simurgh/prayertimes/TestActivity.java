package com.simurgh.prayertimes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cleveroad.slidingtutorial.Direction;
import com.cleveroad.slidingtutorial.IndicatorOptions;
import com.cleveroad.slidingtutorial.PageOptions;
import com.cleveroad.slidingtutorial.Renderer;
import com.cleveroad.slidingtutorial.TransformItem;
import com.cleveroad.slidingtutorial.TutorialFragment;
import com.cleveroad.slidingtutorial.TutorialOptions;
import com.cleveroad.slidingtutorial.TutorialPageOptionsProvider;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * Created by moshe on 13/07/2017.
 */



public class TestActivity extends Activity {

    private int[] mPagesColors;

    TextView tvSkip;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        tvSkip = (TextView)findViewById(R.id.tvSkip);

        mPagesColors = new int[]{
                ContextCompat.getColor(this, android.R.color.darker_gray),
                ContextCompat.getColor(this, android.R.color.holo_green_dark),
                ContextCompat.getColor(this, android.R.color.holo_red_dark),
                ContextCompat.getColor(this, android.R.color.holo_blue_dark),
                ContextCompat.getColor(this, android.R.color.holo_purple),
                ContextCompat.getColor(this, android.R.color.holo_orange_dark),
        };



        final IndicatorOptions indicatorOptions = IndicatorOptions.newBuilder(getApplicationContext())
                .setElementColorRes(R.color.white)
                .setSelectedElementColorRes(R.color.grey)
	            .setRenderer(new Renderer() {
                    @Override
                    public void draw(@NonNull Canvas canvas, @NonNull RectF elementBounds, @NonNull Paint paint, boolean isActive) {
                        float radius = Math.min(elementBounds.width(), elementBounds.height());
                        radius /= 2f;
                        canvas.drawCircle(elementBounds.centerX(), elementBounds.centerY(), radius, paint);
                    }
                })
	            .build();

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
                    case 7: {
                        pageLayoutResId = R.layout.intro_ninth;
                        tutorialItems = new TransformItem[]{
                                TransformItem.create(R.id.iv_main, Direction.LEFT_TO_RIGHT, 0.2f)
                        };
                        tvSkip.setVisibility(View.VISIBLE);
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("Unknown position: " + position);
                    }
                }

                return PageOptions.create(pageLayoutResId, position, tutorialItems);
            }
        };

        final TutorialOptions tutorialOptions = TutorialFragment.newTutorialOptionsBuilder(getApplicationContext())
                .setUseAutoRemoveTutorialFragment(false)
                .setUseInfiniteScroll(false)
                .setTutorialPageProvider(tutorialPageOptionsProvider)
                .setIndicatorOptions(indicatorOptions)
                .setPagesCount(8)
                .build();

        final TutorialFragment tutorialFragment = TutorialFragment.newInstance(tutorialOptions);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, tutorialFragment)
                .commit();


        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(main);
                finish();
            }
        });
    }

}
