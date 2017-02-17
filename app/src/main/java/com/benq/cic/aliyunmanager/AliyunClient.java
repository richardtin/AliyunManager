package com.benq.cic.aliyunmanager;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.ListObjectsRequest;
import com.alibaba.sdk.android.oss.model.ListObjectsResult;

import java.util.ArrayList;

public class AliyunClient {

    private OSS mOssClient;

    public AliyunClient(Context context) {
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(AliyunOSS.accessKeyId, AliyunOSS.accessKeySecret);
        mOssClient = new OSSClient(context, AliyunOSS.endpoint, credentialProvider);
    }

    public ArrayList<String> getAllBucketItems() {
        ListObjectsRequest listObjects = new ListObjectsRequest(AliyunOSS.bucket);

        ArrayList<String> listItems = getListObjects(listObjects);

        return listItems;
    }

    public ArrayList<String> getListItemsWithPrefix(String prefix) {
        ListObjectsRequest listObjects = new ListObjectsRequest(AliyunOSS.bucket);
        listObjects.setPrefix(prefix);
        listObjects.setDelimiter(AliyunOSS.delimiter);

        ArrayList<String> listItems = getListObjects(listObjects);

        return listItems;
    }

    private ArrayList<String> getListObjects(ListObjectsRequest request) {
        ArrayList<String> listItems = new ArrayList<>();

        try {
            ListObjectsResult result = mOssClient.listObjects(request);

            for (int i = 0; i < result.getCommonPrefixes().size(); i++) {
                String folder = result.getCommonPrefixes().get(i);
                Log.d("getListObjects", "folder: " + folder);
                listItems.add(folder);
            }
            for (int i = 0; i < result.getObjectSummaries().size(); i++) {
                String file = result.getObjectSummaries().get(i).getKey();
                Log.d("getListObjects", "object: " + result.getObjectSummaries().get(i).getKey() + " "
                        + result.getObjectSummaries().get(i).getETag() + " "
                        + result.getObjectSummaries().get(i).getLastModified());
                if (!file.endsWith("/")) {
                    listItems.add(file);
                }
            }
        }
        catch (ClientException clientException) {
            clientException.printStackTrace();
        }
        catch (ServiceException serviceException) {
            Log.e("ErrorCode", serviceException.getErrorCode());
            Log.e("RequestId", serviceException.getRequestId());
            Log.e("HostId", serviceException.getHostId());
            Log.e("RawMessage", serviceException.getRawMessage());
        }

        return listItems;
    }

    public String getObjectUrl(String objectKey) {
        String objectUrl = null;
        try {
            objectUrl = mOssClient.presignConstrainedObjectURL(AliyunOSS.bucket, objectKey, 5 * 60);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return objectUrl;
    }

}
