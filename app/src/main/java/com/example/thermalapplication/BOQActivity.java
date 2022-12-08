package com.example.thermalapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class BOQActivity extends UpasargaActivity implements View.OnClickListener, BOQPresenter.View {

    private android.support.v7.widget.Toolbar toolbar;
    private LinearLayout topParentLayout;
    private TextView toolbarTitle;
    private ImageView backArrow;
    private ImageView boqCompare;
    private SmoothProgressBar progressBar;
    private LinearLayout emptyDataSetView;
    private List<AccessFeature.Datum.Permission> permission;
    private BOQRecyclerViewAdapter boqRecyclerViewAdapter;
    private RecyclerView recyclerView;
    private BOQPresenter boqPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boq);
        initiliseView();
        initialiseListener();
        setUpRecyclerView();
        checkAvailableData();

    }


    @Override
    protected void initiliseView() {


        toolbar = findViewById(R.id.toolbar);
        topParentLayout = findViewById(R.id.top_parent_layout);
        toolbarTitle = findViewById(R.id.toolbar_title);
        backArrow = findViewById(R.id.back_arrow);
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        emptyDataSetView = findViewById(R.id.empty_data_set);
        boqCompare = findViewById(R.id.boq_compare);

    }

    @Override
    protected void initialiseListener() {

        toolbarTitle.setOnClickListener(this);
        backArrow.setOnClickListener(this);
        boqCompare.setOnClickListener(this);
        boqPresenter = new BOQPresenter(this);

    }


    private void checkAvailableData() {
        if (UtilitiesFunctions.isNetworkAvailable(BOQActivity.this)) {
            if (Utilities.getBOQData() != null) {
                setUpBOQData(Utilities.getBOQData());
            }
            showProgressBarWork();
            boqPresenter.getBOQs();

        } else {
            if (Utilities.getBOQData() != null) {
                setUpBOQData(Utilities.getBOQData());
            }
            UpasargaToast.showToastWithMessage(getString(R.string.check_your_internet));
        }
    }

    private void setUpBOQData(BOQModel boqData) {

        boqRecyclerViewAdapter.clear();
        boqRecyclerViewAdapter.add(boqData);
        boqRecyclerViewAdapter.notifyDataSetChanged();
        hideProgressBarWork();

    }


    private void showProgressBarWork() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBarWork() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.back_arrow:
            case R.id.toolbar_title:
                finish();
                break;


            case R.id.boq_compare:



                break;

        }

    }


    private void setUpRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BOQActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        boqRecyclerViewAdapter = new BOQRecyclerViewAdapter();
        recyclerView.setAdapter(boqRecyclerViewAdapter);
        boqRecyclerViewAdapter.registerAdapterDataObserver(new RecyclerViewEmptyDataObserver(recyclerView, emptyDataSetView));

    }

    @Override
    public void onBOQSuccess(BOQModel boqModel) {

        setUpBOQData(boqModel);

    }

    @Override
    public void onFailure(String message) {

        UpasargaToast.showToastWithMessage(message);
        hideProgressBarWork();

    }


    public class BOQRecyclerViewAdapter extends UpasargaRecyclerViewAdapter {

        private final int TYPE_HEADER = 0;
        private final int TYPE_ITEMS = 1;

        private BOQModel boqModel;

        @Override
        protected void add(Object object) {

            this.boqModel = UpasargaType.getType(object, BOQModel.class);

        }

        @Override
        protected void clear() {

            boqModel = null;
            notifyDataSetChanged();

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            if (viewType == TYPE_HEADER) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_boq_content_header, parent, false);
                return new VHHeaderTitle(view);

            } else if (viewType == TYPE_ITEMS) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_boq_content, parent, false);

                return new VHItems(view);

            }
            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            if (holder instanceof VHItems) {
                VHItems vhItems = (VHItems) holder;
                BOQModel.Datum.Boq boq = boqModel.getData().get(0).getBoq().get(position - 1);
                vhItems.boqTitle.setText(boq.getTitle());
                vhItems.boqLabel.setText(boq.getLabel());
                vhItems.boqQuantity.setText(String.valueOf(boq.getQuantity()));
                vhItems.boqRate.setText(String.valueOf(boq.getRate()));

            }

        }


        @Override
        public int getItemViewType(int position) {
            if (isPositionHeader(position)) {

                return TYPE_HEADER;
            }

            return TYPE_ITEMS;

        }

        private boolean isPositionHeader(int position) {
            return position == 0;
        }

        @Override
        public int getItemCount() {
            if (boqModel != null) {
                if (boqModel.getData() != null) {

                    if (boqModel.getData().size() > 0) {
                        return boqModel.getData().get(0).getBoq().size() + 1;

                    }


                }

            }
            return 0;
        }


        private class VHItems extends RecyclerView.ViewHolder implements View.OnClickListener {

            private TextView boqTitle;
            private TextView boqLabel;
            private TextView boqQuantity;
            private TextView boqRate;

            public VHItems(View view) {
                super(view);

                boqTitle = view.findViewById(R.id.boq_title);
                boqLabel = view.findViewById(R.id.boq_label);
                boqQuantity = view.findViewById(R.id.boq_quantity);
                boqRate = view.findViewById(R.id.boq_rate);


            }

            @Override
            public void onClick(View view) {
                switch (view.getId()) {


                }
            }


        }

        private class VHHeaderTitle extends RecyclerView.ViewHolder {
            public VHHeaderTitle(View view) {
                super(view);
            }
        }
    }


}