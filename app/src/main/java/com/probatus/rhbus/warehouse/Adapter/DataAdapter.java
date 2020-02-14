package com.probatus.rhbus.warehouse.Adapter;

/**
 * Created by Ganapathi on 11-03-2018.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.probatus.rhbus.warehouse.Interface.ItemClickListener;
import com.probatus.rhbus.warehouse.Interface.productversion;
import com.probatus.rhbus.warehouse.JsonParse.MyStatic;
import com.probatus.rhbus.warehouse.R;
import com.probatus.rhbus.warehouse.fragment.WMSGRListFragment;
import com.probatus.rhbus.warehouse.fragment.WMSPickListFragment;
import com.probatus.rhbus.warehouse.fragment.WMSTransferListFragment;
import com.viethoa.RecyclerViewFastScroller;

import java.util.ArrayList;
import java.util.Locale;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder>
        implements RecyclerViewFastScroller.BubbleTextGetter{
    private ItemClickListener clickListener;
    private ArrayList<productversion> android;
    private ArrayList<productversion> arraylist = new ArrayList<productversion>();
    private Context context;


    public DataAdapter(Context context, ArrayList<productversion> android) {
        this.android = android;
        this.context = context;
        this.arraylist=new ArrayList<productversion>();
        if(MyStatic.getWhichactivity().equals("GOODSRECIEPT")) {
            this.arraylist.addAll(WMSGRListFragment.productversions);
        } else if(MyStatic.getWhichactivity().equals("PICKORDER")){
            this.arraylist.addAll(WMSPickListFragment.productversions);
        } else if(MyStatic.getWhichactivity().equals("TRANSFER")){
            this.arraylist.addAll(WMSTransferListFragment.productversions);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.location_card, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder,final int i) {
        final productversion productversion = android.get(i);
        viewHolder.tv_android.setText(productversion.getProduct_itemcode());
        if(MyStatic.getWhichactivity().equals("GOODSRECIEPT")){
            viewHolder.tv_custVend.setText("Vendor");
        } else if(MyStatic.getWhichactivity().equals("PICKORDER")){
            viewHolder.tv_custVend.setText("Customer");
        } else if(MyStatic.getWhichactivity().equals("TRANSFER")){
            viewHolder.tv_custVend.setText("User");
            viewHolder.tv_label2.setText("Location");
            viewHolder.tv_label3.setText("SU Code");
        }
        viewHolder.tv_desc.setText(productversion.getAndroid_version_name());
        viewHolder.tv_count.setText(productversion.getProduct_lineno());
        viewHolder.tv_date.setText(productversion.getProduct_brand());
        viewHolder.tv_FromOrder.setText(productversion.getProduct_category());
        viewHolder.tv_status.setText(productversion.getProduct_subcategory());
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        if (pos < 0 || pos >= android.size())
            return null;

        String name = android.get(pos).getProduct_subcategory();
        if (name == null || name.length() < 1)
            return null;

        return android.get(pos).getProduct_subcategory().substring(0, 1);
    }

    @Override
    public int getItemCount() {
        return android== null ? 0 : android.size();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView tv_android, tv_desc, tv_count,tv_date,tv_FromOrder,tv_status;
        public TextView tv_custVend, tv_label2, tv_label3;
        public ViewHolder(final View view) {
            super(view);
            tv_android = (TextView)view.findViewById(R.id.txtDoc);
            tv_desc = (TextView)view.findViewById(R.id.txtName);
            tv_count = (TextView)view.findViewById(R.id.txtLoc1);
            tv_date = (TextView)view.findViewById(R.id.txtLoc2);
            tv_FromOrder = (TextView)view.findViewById(R.id.txtLoc3);
            tv_status = (TextView)view.findViewById(R.id.txtLoc4);

            tv_custVend = (TextView)view.findViewById(R.id.tv_custVendlel);
            tv_label2 = (TextView)view.findViewById(R.id.tv_label2);
            tv_label3 = (TextView)view.findViewById(R.id.tv_label3);

            view.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            /* CustomCheckBox ckbox=(CustomCheckBox)view.findViewById(R.id.img_added);
            ckbox.setChecked(!ckbox.isChecked());*/
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }
    }

    public void filter(String charText,Integer search) {
        try{
            if(MyStatic.getWhichactivity().equals("GOODSRECIEPT")) {
                WMSGRListFragment.productversions.clear();
            } else if(MyStatic.getWhichactivity().equals("PICKORDER")){
                WMSPickListFragment.productversions.clear();
            } else if(MyStatic.getWhichactivity().equals("TRANSFER")){
                WMSTransferListFragment.productversions.clear();
            }
        }catch (Exception e){
        }
        if (charText.length() == 0) {
            if(MyStatic.getWhichactivity().equals("GOODSRECIEPT")) {
                WMSGRListFragment.productversions.addAll(arraylist);
            } else if(MyStatic.getWhichactivity().equals("PICKORDER")){
                WMSPickListFragment.productversions.addAll(arraylist);
            } else if(MyStatic.getWhichactivity().equals("TRANSFER")){
                WMSTransferListFragment.productversions.addAll(arraylist);
            }
        } else {
            switch (search) {
                case 1:
                    for (productversion wp : arraylist) {
                        if (wp.getProduct_description().toLowerCase(Locale.getDefault()).contains(charText)) {
                            if(MyStatic.getWhichactivity().equals("GOODSRECIEPT")) {
                                WMSGRListFragment.productversions.add(wp);
                            } else if(MyStatic.getWhichactivity().equals("PICKORDER")){
                                WMSPickListFragment.productversions.add(wp);
                            } else if(MyStatic.getWhichactivity().equals("TRANSFER")){
                                WMSTransferListFragment.productversions.add(wp);
                            }
                        }
                    }
                    notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
        notifyDataSetChanged();
    }

}
