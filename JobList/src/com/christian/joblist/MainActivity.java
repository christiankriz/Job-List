package com.christian.joblist;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.entity.ByteArrayEntity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity {
	JobListTask jobListTask;
	static ListView list;
    static JobListAdapter adapter;
    static public  MainActivity mainActivity = null;
    static ProgressDialog progress;
    public static  ArrayList<Jobs> jobList = new ArrayList<Jobs>();
    static EditText searchField;
    static Button nextButton;
    static int listSize, numOfScreens,currentPos, screenNum;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		
		mainActivity = this;
		
		  /**************** Create Custom Adapter *********/
		progress = ProgressDialog.show(this, "Job List",
			    "Please wait while fetching jobs.....", true);
        
        jobListTask = new JobListTask();
		jobListTask.execute();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			
			 list = ( ListView ) rootView.findViewById(R.id.Job_list);
             searchField = (EditText) rootView.findViewById(R.id.search_input);
             nextButton = (Button) rootView.findViewById(R.id.next_button);
            // searchButton.setVisibility(View.GONE);
             nextButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mainActivity.loadList();
					
				}
			});
    		return rootView;
		}
	}
	
	public class JobListTask extends AsyncTask<Void, Void, Boolean> {

		JobListTask() {

		}

		@Override
		protected Boolean doInBackground(Void... params) {
			SyncHttpClient client = new SyncHttpClient();
			String serviceUrl = "https://gist.githubusercontent.com/WillemLabu/34cfb50187ec334c48ee/raw/84f2ebf1c2343a23792d725ef3da4a9c61f10857/jobs.json";
			
			client.addHeader("Accept", "application/json");
			client.addHeader("Content-type", "application/json");
			client.get(serviceUrl, new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2,
						Throwable arg3) {
					progress.dismiss();

					arg3.printStackTrace();
					String error = arg3.getMessage();
					setErrorMsge(error);

				}

				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					// TODO Auto-generated method stub

					String response = null;
					try {
						response = new String(arg2, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Type colType = new TypeToken<ArrayList<Jobs>>() {
					}.getType();
					Gson gson = new Gson();
					JsonObject jo = new JsonParser().parse(response)
							.getAsJsonObject();
					JsonArray jsonArray = jo.getAsJsonArray("jobs");

					jobList  = gson.fromJson(jsonArray, colType);
					
					

				}
			});

			return true;
		}

	     public void setErrorMsge(String errorMsge){
	       	 AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
	       		builder.setMessage(errorMsge)
	       				.setCancelable(false)
	       				.setPositiveButton("Ok",
	       						new DialogInterface.OnClickListener() {
	       							public void onClick(DialogInterface dialog,
	       									int id) {
	       								dialog.cancel();
	       							}
	       						});
	       		AlertDialog alert = builder.create();
	       		alert.show();
	       		
	            return;
	       	 
	        }
	     

		@Override
		protected void onPostExecute(final Boolean success) {
			// prStatus.cancel();
			// goBack();
			  /**************** Create Custom Adapter *********/
			if(progress != null)
				  progress.dismiss();
			listSize = jobList.size();
			int remender = jobList.size() % 10;
			numOfScreens = jobList.size() / 10;
			if(remender > 0)
				numOfScreens = (jobList.size()/10) + 1;
		    currentPos = 0;
		    screenNum = 0;
		    loadList();
			searchField.addTextChangedListener(new TextWatcher() {
	             
	             @Override
	             public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
	                 // When user changed the Text
	            	 String searchWord = cs.toString();
	            	 ArrayList<Jobs> newList = new ArrayList<Jobs>();
                     String jobName;
                     
                     Jobs job = null;
                     for (int i = 0; i < jobList.size(); i++) {
                         job = (Jobs) jobList.get(i);
                         jobName = job.getJobName().toLowerCase();
                         if (jobName.indexOf(searchWord.toLowerCase()) != -1) {
                             newList.add(job);
                         }
                     }
                     if (newList.size() > 0) {
                    	adapter=new JobListAdapter(mainActivity, newList,null );
             	        list.setAdapter( adapter );
             			adapter.notifyDataSetChanged();
             			nextButton.setVisibility(View.GONE);
                     }
                }
	              
	             @Override
	             public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
	                     int arg3) {
	                 // TODO Auto-generated method stub
	                  
	             }
	              
	             @Override
	             public void afterTextChanged(Editable arg0) {
	                 // TODO Auto-generated method stub                          
	             }
	         });
		}

		@Override
		protected void onCancelled() {

		}
	}
	public void loadList(){
		boolean lastScreen = false;
		ArrayList<Jobs> tenJobList= null;
		progress = ProgressDialog.show(this, "Job List",
			    "loading list.....", true);
		
		if((currentPos + 10)  > (jobList.size() - currentPos) && jobList.size() > 10)
			lastScreen = true;
		if(lastScreen){
			tenJobList = new ArrayList<Jobs>(jobList.subList(currentPos, jobList.size() - 1));
			adapter=new JobListAdapter(mainActivity, tenJobList,null );
		    list.setAdapter( adapter );
			adapter.notifyDataSetChanged();
			nextButton.setVisibility(View.VISIBLE);
			nextButton.setText("Done");
			currentPos = 0;
			screenNum = 0;
		}if(jobList.size() <= 10){
			adapter=new JobListAdapter(mainActivity, jobList,null );
		    list.setAdapter( adapter );
			adapter.notifyDataSetChanged();
		}if(!lastScreen && listSize > 10){
			tenJobList = new ArrayList<Jobs>(jobList.subList(currentPos, currentPos + 10));
			screenNum++;
			currentPos += 10;
			nextButton.setVisibility(View.VISIBLE);
			nextButton.setText(screenNum + "/" + numOfScreens + " >>");
			adapter=new JobListAdapter(mainActivity, tenJobList,null );
		    list.setAdapter( adapter );
		    adapter.notifyDataSetChanged();
		}
		  
		  
		  if(progress != null)
			  progress.dismiss();
		  
	}

}
