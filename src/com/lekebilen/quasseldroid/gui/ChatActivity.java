package com.lekebilen.quasseldroid.gui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lekebilen.quasseldroid.R;
import com.lekebilen.quasseldroid.communication.CoreConnService;

public class ChatActivity extends Activity{
	
	
	public static final int MESSAGE_RECEIVED = 0;
	
	private BacklogAdapter adapter;
	private static final String TAG = ChatActivity.class.getSimpleName();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_layout);
		
		
		//Populate with test data
		((TextView)findViewById(R.id.chatNameView)).setText("#mtdt12");

		adapter = new BacklogAdapter(this, null);
		adapter.addItem(new BacklogEntry("1", "nr1", "Woo"));
		adapter.addItem(new BacklogEntry("2", "n2", "Weee"));
		adapter.addItem(new BacklogEntry("3", "nr3", "Djiz"));
		adapter.addItem(new BacklogEntry("4", "nr4", "Pfft"));
		adapter.addItem(new BacklogEntry("5", "nr5", "Meh"));
		adapter.addItem(new BacklogEntry("6", "nr6", ":D"));
		adapter.addItem(new BacklogEntry("7", "nr7", "Hax"));
		adapter.addItem(new BacklogEntry("8", "nr8", "asdasa sdasd asd asds a"));
		adapter.addItem(new BacklogEntry("9", "nr9", "MER SPAM"));
		((ListView)findViewById(R.id.chatBacklogList)).setAdapter(adapter);
		
		//Service testing
		this.doBindService();
		
		
	}
	
	private class BacklogAdapter extends BaseAdapter {
		
		private ArrayList<BacklogEntry> backlog;
		private LayoutInflater inflater;

		
		public BacklogAdapter(Context context, ArrayList<BacklogEntry> backlog) {
			if (backlog==null) {
				this.backlog = new ArrayList<BacklogEntry>();
			}else {
				this.backlog = backlog;				
			}
			inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}
		
		public void addItem(BacklogEntry item) {
			Log.i(TAG, item.time);
            this.backlog.add(item);
            notifyDataSetChanged();
        }


		@Override
		public int getCount() {
			return backlog.size();
		}

		@Override
		public BacklogEntry getItem(int position) {
			return backlog.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView==null) {
				convertView = inflater.inflate(R.layout.backlog_item, null);
				holder = new ViewHolder();
				holder.timeView = (TextView)convertView.findViewById(R.id.backlog_time_view);
				holder.nickView = (TextView)convertView.findViewById(R.id.backlog_nick_view);
				holder.msgView = (TextView)convertView.findViewById(R.id.backlog_msg_view);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder)convertView.getTag();
			}
			BacklogEntry entry = backlog.get(position);
			holder.timeView.setText(entry.time);
			holder.nickView.setText(entry.nick);
			holder.msgView.setText(entry.msg);
			return convertView;
		}

		
	}
	
	
	// The Handler that gets information back from the CoreConnService
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
	            case MESSAGE_RECEIVED:
	            	com.lekebilen.quasseldroid.Message message = (com.lekebilen.quasseldroid.Message) msg.obj;
	            	adapter.addItem(new BacklogEntry(message.timestamp.toString(), message.sender, message.content));
	                break;
            }
        }
    };
	
	
	public static class ViewHolder {
        public TextView timeView;
        public TextView nickView;
        public TextView msgView;
    }
	
	public class BacklogEntry {
		public String time;
		public String nick;
		public String msg;
		
		public BacklogEntry(String time, String nick, String msg) {
			this.time = time;
			this.nick = nick;
			this.msg = msg;
		}
	}
	
	/**
	 * Code for service binding:
	 */
	private CoreConnService boundConnService;
	private Boolean isBound;
	
	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	    	Log.i(TAG, "BINDING ON SERVICE DONE");
	        boundConnService = ((CoreConnService.LocalBinder)service).getService();

	    }

	    public void onServiceDisconnected(ComponentName className) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        // Because it is running in our same process, we should never
	        // see this happen.
	    	boundConnService = null;
	        
	    }
	};

	void doBindService() {
	    // Establish a connection with the service.  We use an explicit
	    // class name because we want a specific service implementation that
	    // we know will be running in our own process (and thus won't be
	    // supporting component replacement by other applications).
	    bindService(new Intent(ChatActivity.this, CoreConnService.class), mConnection, Context.BIND_AUTO_CREATE);
	    isBound = true;
	}

	void doUnbindService() {
	    if (isBound) {
	        // Detach our existing connection.
	        unbindService(mConnection);
	        isBound = false;
	    }
	}



}