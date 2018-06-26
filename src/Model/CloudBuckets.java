package Model;

import com.google.cloud.storage.Bucket;

public class CloudBuckets {
    private Bucket orignalBucket;
    private String bucketName;

    public CloudBuckets (Bucket orignalBucket) {
        this.orignalBucket = orignalBucket;
        this.bucketName = this.orignalBucket.toString();
    }

    public Bucket getOrignalBucket() {
        return orignalBucket;
    }

    public String getBucketName() {
        return bucketName;
    }
}
