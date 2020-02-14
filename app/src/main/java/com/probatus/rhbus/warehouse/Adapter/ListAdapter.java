package com.probatus.rhbus.warehouse.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.probatus.rhbus.warehouse.Interface.AdapterCallback;
import com.probatus.rhbus.warehouse.Interface.ItemClickListener;
import com.probatus.rhbus.warehouse.Interface.UserModel;
import com.probatus.rhbus.warehouse.JsonParse.MyStatic;
import com.probatus.rhbus.warehouse.R;
import com.probatus.rhbus.warehouse.Interface.productversion;
import com.probatus.rhbus.warehouse.WMSGRCard;
import com.probatus.rhbus.warehouse.WMSPickCard;
import com.probatus.rhbus.warehouse.WMSTransferCard;
import com.probatus.rhbus.warehouse.fragment.WMSGRListFragment;
import com.probatus.rhbus.warehouse.fragment.WMSPickListFragment;
import com.viethoa.RecyclerViewFastScroller;
import com.volcaniccoder.volxfastscroll.IVolxAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by ganesh on 7/4/18.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>
        implements RecyclerViewFastScroller.BubbleTextGetter {

    private ArrayList<productversion> selected_product = new ArrayList<productversion>();
    private ArrayList<productversion> arraylist = new ArrayList<>();
    private AdapterCallback mAdapterCallback;
    private Context context;
    public boolean lastposition=true,isdelete=true;
    public int lastpos;


    public ListAdapter(Context context,ArrayList<productversion> selected_product) {
        this.selected_product = selected_product;
        this.context=context;
        try {
            this.mAdapterCallback = ((AdapterCallback) context);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
        this.arraylist = new ArrayList<>();
        if(MyStatic.getWhichactivity().equals("GOODSRECIEPT")) {
            this.arraylist.addAll(WMSGRCard.productversions);
        } else if(MyStatic.getWhichactivity().equals("PICKORDER")){
            this.arraylist.addAll(WMSPickCard.productversions);
        } else if(MyStatic.getWhichactivity().equals("TRANSFER")){
            this.arraylist.addAll(WMSTransferCard.productversions);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_layout_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder,final int i) {
        final productversion selected = selected_product.get(i);
        viewHolder.list_product_itemcode.setText(selected.getProduct_itemcode());
        viewHolder.list_product_desc.setText(selected.getProduct_description());
        viewHolder.list_product_qantity.setText(roundTwoDecimals(selected.getProduct_quantity()));
        viewHolder.list_product_exqantity.setText(roundTwoDecimals(selected.getProduct_delete()));
        if(MyStatic.getWhichactivity().equals("GOODSRECIEPT")){
            viewHolder.list_product_avbqty.setText("PO Qty:"+ roundTwoDecimals(selected.getProduct_brand()));
        } else if(MyStatic.getWhichactivity().equals("PICKORDER")) {
            viewHolder.list_product_avbqty.setText("Avl Qty:"+roundTwoDecimals(selected.getProduct_linedisc()));
        } else if(MyStatic.getWhichactivity().equals("TRANSFER")) {
            viewHolder.list_product_avbqty.setText("Snd Qty:"+roundTwoDecimals(selected.getProduct_linedisc()));
        }
        viewHolder.list_product_lot.setText(selected.getProduct_total());

        if(MyStatic.getWhichactivity().equals("GOODSRECIEPT")){
            viewHolder.list_product_sucode.setText("Recved Qty:"+roundTwoDecimals(selected.getProduct_linedisc()));
        } else if(MyStatic.getWhichactivity().equals("PICKORDER")) {
            viewHolder.list_product_sucode.setText(selected.getProduct_brand());
        } else if(MyStatic.getWhichactivity().equals("TRANSFER")) {
            viewHolder.list_product_sucode.setText(selected.getProduct_category());
        }
        viewHolder.list_product_baseuom.setText(selected.getProduct_hsn());

        viewHolder.list_product_qantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Line = selected.getProduct_lineno();
                String LotLine = selected.getProduct_barcode();
                mAdapterCallback.onMethodCallback(Line,LotLine,viewHolder.list_product_qantity,null,"qt",i);
            }
        });

        viewHolder.weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Line = selected.getProduct_lineno();
                String LotLine = selected.getProduct_barcode();
                mAdapterCallback.onMethodCallback(Line,LotLine,viewHolder.list_product_qantity,null,"weight",i);
            }
        });

        viewHolder.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Line = selected.getProduct_lineno();
                String LotLine = selected.getProduct_barcode();
                mAdapterCallback.onMethodCallback(Line,LotLine,viewHolder.list_product_qantity,null,"addmore",i);
            }
        });

        viewHolder.redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Line = selected.getProduct_lineno();
                String LotLine = selected.getProduct_barcode();
                mAdapterCallback.onMethodCallback(Line,LotLine,viewHolder.list_product_qantity,null,"redo",i);
            }
        });
    }

    String roundTwoDecimals(String d) {
        DecimalFormat twoDForm = new DecimalFormat("####0.00");
        try {
            return twoDForm.format(Double.valueOf(d)).toString();
        } catch (NullPointerException e){
            return "0.00";
        } catch (Exception e){
            return "0.00";
        }
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        if (pos < 0 || pos >= selected_product.size())
            return null;

        String name = selected_product.get(pos).getAndroid_version_name();
        if (name == null || name.length() < 1)
            return null;

        return selected_product.get(pos).getAndroid_version_name().substring(0, 1);
    }

    @Override
    public int getItemCount() {
        return selected_product.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView list_product_itemcode;
        TextView list_product_desc;
        TextView list_product_qantity, tv_reqQty;
        TextView list_product_exqantity, tv_list_labelreq, list_product_lot;
        TextView list_product_avbqty, list_product_sucode, list_product_baseuom;
        FancyButton weight, confirm, redo;
        public ViewHolder(final View view) {
            super(view);
            list_product_itemcode = (TextView) view.findViewById(R.id.tv_list_itemcode);
            list_product_desc = (TextView) view.findViewById(R.id.tv_list_desc);
            list_product_qantity = (TextView) view.findViewById(R.id.tv_list_qantity);
            list_product_exqantity = (TextView) view.findViewById(R.id.tv_list_unit);
            tv_list_labelreq = (TextView) view.findViewById(R.id.tv_list_labelreq);
            list_product_avbqty = (TextView) view.findViewById(R.id.tv_list_avl);
            list_product_lot = (TextView) view.findViewById(R.id.tv_list_lot);
            list_product_sucode = (TextView) view.findViewById(R.id.tv_list_sucode);
            list_product_baseuom = (TextView) view.findViewById(R.id.tv_list_uom);
            tv_reqQty = (TextView) view.findViewById(R.id.tv_reqQty);

            weight = (FancyButton) view.findViewById(R.id.button1);
            confirm = (FancyButton) view.findViewById(R.id.button2);
            redo = (FancyButton) view.findViewById(R.id.button3);

            if(MyStatic.getWhichactivity().equals("GOODSRECIEPT")){
                tv_list_labelreq.setText("Receiving Quanity");
            } else if(MyStatic.getWhichactivity().equals("PICKORDER")) {
                tv_list_labelreq.setText("Quanity to Pick");
            } else if(MyStatic.getWhichactivity().equals("TRANSFER")) {
                tv_list_labelreq.setText("Quanity to Sent");
                tv_reqQty.setText("Remaining Quantity");
            }
        }
    }


    public void filter(String charText,Integer search) {
        charText = charText.toLowerCase(Locale.getDefault());
        try{
            if(MyStatic.getWhichactivity().equals("GOODSRECIEPT")) {
                WMSGRCard.productversions.clear();
            } else if(MyStatic.getWhichactivity().equals("PICKORDER")){
                WMSPickCard.productversions.clear();
            } else if(MyStatic.getWhichactivity().equals("TRANSFER")){
                WMSTransferCard.productversions.clear();
            }
        }catch (Exception e){
        }
        if (charText.length() == 0) {
            if(MyStatic.getWhichactivity().equals("GOODSRECIEPT")) {
                WMSGRCard.productversions.addAll(arraylist);
            } else if(MyStatic.getWhichactivity().equals("PICKORDER")){
                WMSPickCard.productversions.addAll(arraylist);
            } else if(MyStatic.getWhichactivity().equals("TRANSFER")){
                WMSTransferCard.productversions.addAll(arraylist);
            }
        } else {
            switch (search) {
                case 1:
                    for (productversion wp : arraylist) {
                        if (wp.getProduct_uom().toLowerCase(Locale.getDefault()).contains(charText)) {
                            if(MyStatic.getWhichactivity().equals("GOODSRECIEPT")) {
                                WMSGRCard.productversions.add(wp);
                            } else if(MyStatic.getWhichactivity().equals("PICKORDER")){
                                WMSPickCard.productversions.add(wp);
                            } else if(MyStatic.getWhichactivity().equals("PICKORDER")){
                                WMSTransferCard.productversions.add(wp);
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
