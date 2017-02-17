package com.benq.cic.aliyunmanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ListFragment extends Fragment {

    private String mOssPrefix = "";
    private ListView mListView;
    private TextView mEmptyView;
    private OnListFragmentInteractionListener mListener;

    public ListFragment() {
    }

    public static ListFragment newInstance(String prefix) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString("oss_prefix", prefix);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OnListFragmentInteractionListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mOssPrefix = getArguments().getString("oss_prefix", "");
        }
        Log.d("ListFragment", "onCreate(): oss_prefix = " + mOssPrefix);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        mListView = (ListView) rootView.findViewById(R.id.list);
        mEmptyView = (TextView) rootView.findViewById(R.id.empty);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mOssPrefix.isEmpty() ? "Home" : mOssPrefix);
            actionBar.setDisplayHomeAsUpEnabled(mOssPrefix.isEmpty() ? false : true);
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        AsyncTask<Void, Void, ArrayList<String>> getItemsTask = new AsyncTask<Void, Void, ArrayList<String>>() {
            final Context context = getContext();
            final ProgressDialog dialog = new ProgressDialog(context);

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Loading...");
                dialog.show();
            }

            @Override
            protected ArrayList<String> doInBackground(Void... voids) {
                AliyunClient client = new AliyunClient(context);
                return client.getListItemsWithPrefix(mOssPrefix);
//                return client.getAllBucketItems();
            }

            @Override
            protected void onPostExecute(ArrayList<String> items) {
                dialog.dismiss();
                if (!items.isEmpty()) {
                    mListView.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
                ArrayAdapter<String> listAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, items);
                mListView.setAdapter(listAdapter);
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String item = parent.getAdapter().getItem(position).toString();
                        if (item.endsWith("/")) {
                            mListener.onListFragmentInteraction(item);
                        } else {
                            AliyunClient client = new AliyunClient(context);
                            String objectUrl = client.getObjectUrl(item);
                            Log.d("GetObjectUrl", "objectUrl = " + objectUrl);
                            Toast.makeText(getContext(), item, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
        getItemsTask.execute();

    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(String item);
    }
}
