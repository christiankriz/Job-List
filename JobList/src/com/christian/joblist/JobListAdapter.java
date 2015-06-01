package com.christian.joblist;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class JobListAdapter extends BaseAdapter implements OnClickListener {
    
   /*********** Declare Used Variables *********/
   private Activity activity;
   private ArrayList data;
   private static LayoutInflater inflater=null;
   public Resources res;
   Jobs tempValues=null;
   int i=0;
    
   /*************  CustomAdapter Constructor *****************/
   public JobListAdapter(Activity a, ArrayList d,Resources resLocal) {
        
          /********** Take passed values **********/
           activity = a;
           data=d;
           res = resLocal;
        
           /***********  Layout inflator to call external xml layout () ***********/
            inflater = ( LayoutInflater )activity.
                                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
   }

   /******** What is the size of Passed Arraylist Size ************/
   public int getCount() {
        
       if(data.size()<=0)
           return 1;
       return data.size();
   }

   public Object getItem(int position) {
       return position;
   }

   public long getItemId(int position) {
       return position;
   }
    
   /********* Create a holder Class to contain inflated xml file elements *********/
   public static class ViewHolder{
        
       public TextView clientName;
       public TextView jobName;
       public TextView jobNum;
       public TextView jobStatus;
       public TextView jobDueDate;
       public TextView text;
       public ImageView image;

   }

   /****** Depends upon data size called for each row , Create each ListView row *****/
   public View getView(int position, View convertView, ViewGroup parent) {
        
       View vi = convertView;
       ViewHolder holder;
        
       if(convertView==null){
            
           /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
           vi = inflater.inflate(R.layout.job_list_item, null);
            
           /****** View Holder Object to contain tabitem.xml file elements ******/

           holder = new ViewHolder();
           holder.clientName = (TextView) vi.findViewById(R.id.client_name);
           holder.jobNum = (TextView)vi.findViewById(R.id.job_num);
           holder.jobName = (TextView) vi.findViewById(R.id.job_name);
           holder.jobStatus = (TextView)vi.findViewById(R.id.job_status);
           holder.jobDueDate = (TextView)vi.findViewById(R.id.job_due_date);
          // holder.image=(ImageView)vi.findViewById(R.id.image);
            
          /************  Set holder with LayoutInflater ************/
           vi.setTag( holder );
       }
       else 
           holder=(ViewHolder)vi.getTag();
        
       if(data.size()<=0)
       {
           holder.text.setText("No Data");
            
       }
       else
       {
           /***** Get each Model object from Arraylist ********/
           tempValues=null;
           tempValues = ( Jobs ) data.get( position );
            
           /************  Set Model values in Holder elements ***********/

            holder.clientName.setText( tempValues.getClient() );
            holder.jobNum.setText("#" + tempValues.getJobNumber());
            holder.jobName.setText("Job-" + tempValues.getJobName() );
            holder.jobStatus.setText( tempValues.getStatus() );
            holder.jobDueDate.setText("Due-" + tempValues.getDue() );
            
            if(tempValues.getStatus().equals("open")){
            	holder.jobStatus.setTextColor(Color.GREEN);
            }else{
            	holder.jobStatus.setTextColor(Color.RED);
            }
            
            /* holder.image.setImageResource(
                         res.getIdentifier(
                         "com.androidexample.customlistview:drawable/"+tempValues.getImage()
                         ,null,null));*/
             
            /******** Set Item Click Listner for LayoutInflater for each row *******/

            vi.setOnClickListener(new OnItemClickListener( position ));
       }
       return vi;
   }
    
   @Override
   public void onClick(View v) {
           Log.v("CustomAdapter", "=====Row button clicked=====");
   }
    
   /********* Called when Item click in ListView ************/
   private class OnItemClickListener  implements OnClickListener{           
       private int mPosition;
        
       OnItemClickListener(int position){
            mPosition = position;
       }
        
       @Override
       public void onClick(View arg0) {

  
       //  CustomListViewAndroidExample sct = (CustomListViewAndroidExample)activity;

        /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/

          // sct.onItemClick(mPosition);
       }               
   }  
}
