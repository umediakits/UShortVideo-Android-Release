package com.movieous.media.ui.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.OnClick;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.movieous.media.Constants;
import com.movieous.media.R;
import com.movieous.media.mvp.contract.FilterSdkManager;
import com.movieous.media.mvp.model.entity.FilterVendor;
import com.movieous.media.mvp.model.entity.UFilter;
import com.movieous.media.mvp.model.entity.TabEntity;
import io.inchtime.recyclerkit.RecyclerAdapter;
import io.inchtime.recyclerkit.RecyclerKit;
import kotlin.Unit;

import java.util.ArrayList;
import java.util.List;

/**
 * 贴纸选择页面
 */
public class StickerFilterFragment extends BaseFilterFragment implements OnTabSelectListener {

    @BindView(R.id.sticker_filter_recycler)
    RecyclerView mStickerRecyclerView;
    @BindView(R.id.sticker_tab)
    CommonTabLayout mStickerTab;

    private RecyclerAdapter mStickerAdapter;
    private SparseArray<List<RecyclerAdapter.ViewModel>> mViewModels = new SparseArray<>();
    private ImageView mCurrentFilterIcon;
    private FilterSdkManager mFilterSdkManager;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_sticker_filter;
    }

    @Override
    public void initView() {
        initTab();
        setupRecyclerView();
    }

    @OnClick(R.id.sticker_clear)
    public void onClickStickerClear() {
        clearIconState();
        onClearFilter();
    }

    @Override
    public void onTabSelect(int tabIndex) {
        showStickerViewModels(tabIndex);
    }

    @Override
    public void onTabReselect(int position) {
    }

    public void setFilterSdkManager(FilterSdkManager filterSdkManager) {
        mFilterSdkManager = filterSdkManager;
    }

    private void initTab() {
        String[] mTabTitles = mFilterSdkManager.getFilterTypeName();
        ArrayList<CustomTabEntity> tabEntities = new ArrayList<>();
        for (int i = 0; i < mTabTitles.length; i++) {
            tabEntities.add(new TabEntity(mTabTitles[i], 0, 0));
        }
        mStickerTab.setTabData(tabEntities);
        mStickerTab.setOnTabSelectListener(this);
    }

    private void setupRecyclerView() {
        mStickerAdapter = RecyclerKit.INSTANCE.adapter(mActivity, Constants.SPAN_COUNT)
                .recyclerView(mStickerRecyclerView)
                .withGridLayout(GridLayoutManager.VERTICAL, false)
                .modelViewBind((index, viewModel, viewHolder) -> {
                    UFilter item = (UFilter) viewModel.getValue();
                    ImageView imageView = viewHolder.findView(R.id.icon);
                    if (item.getVendor() == FilterVendor.FACEUNITY) {
                        imageView.setImageResource(item.getResId());
                    } else {
                        imageView.setImageBitmap(item.getIcon());
                    }
                    return Unit.INSTANCE;
                })
                .modelViewClick((pIndex, pViewModel, view) -> {
                    clearIconState();
                    ImageView imageView = view.findViewById(R.id.icon);
                    imageView.setBackgroundResource(R.drawable.control_filter_select);
                    mCurrentFilterIcon = imageView;
                    onMagicFilterChanged((UFilter) pViewModel.getValue());
                    return Unit.INSTANCE;
                })
                .emptyViewBind((emptyViewHolder -> Unit.INSTANCE))
                .build();
        mStickerAdapter.setEmptyView(R.layout.recyclerkit_view_empty);
        showStickerViewModels(0);
    }

    private void showStickerViewModels(int typeIndex) {
        List<RecyclerAdapter.ViewModel> models = mViewModels.get(typeIndex);
        if (models == null) {
            models = new ArrayList<>();
            List<UFilter> items = mFilterSdkManager.getMagicFilterList(typeIndex + 1);
            for (UFilter item : items) {
                models.add(new RecyclerAdapter.ViewModel(R.layout.item_filter_view, 1, RecyclerAdapter.ModelType.LEADING, item, false));
            }
            mViewModels.put(typeIndex, models);
        }
        clearIconState();
        mStickerAdapter.setModels(models);
    }

    private void clearIconState() {
        if (mCurrentFilterIcon != null) {
            mCurrentFilterIcon.setBackgroundResource(0);
        }
    }

}
