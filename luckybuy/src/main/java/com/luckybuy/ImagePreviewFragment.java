package com.luckybuy;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;


import org.xutils.common.Callback;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import uk.co.senab.photoview.PhotoViewAttacher;


@ContentView(R.layout.fragment_image_preview)
public class ImagePreviewFragment extends BaseFragment {
	private String mImageUrl;

	@ViewInject(R.id.image_preview)
	private ImageView mImageView;

	@ViewInject(R.id.loading)
	private ProgressBar progressBar;

	//private PhotoViewAttacher mAttacher;

    private ImageOptions imageOptions;

	public static ImagePreviewFragment newInstance(String imageUrl) {
		final ImagePreviewFragment f = new ImagePreviewFragment();

		final Bundle args = new Bundle();
		args.putString("url", imageUrl);
		f.setArguments(args);

		return f;
	}


	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mImageUrl = getArguments() != null ? getArguments().getString("url") : null;
//		mAttacher = new PhotoViewAttacher(mImageView);
//
//		mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
//
//			@Override
//			public void onPhotoTap(View arg0, float arg1, float arg2) {
//				getActivity().finish();
//			}
//		});

        imageOptions = new ImageOptions.Builder()
                .setCrop(true)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .build();

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        progressBar.setVisibility(View.VISIBLE);
        x.image().bind(mImageView, mImageUrl, new Callback.CommonCallback<Drawable>() {


            @Override
            public void onSuccess(Drawable result) {
//                mAttacher.update();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                progressBar.setVisibility(View.GONE);
            }
        });

	/*	ImageLoader.getInstance().displayImage(mImageUrl, mImageView, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				progressBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				String message = null;
				switch (failReason.getType()) {
				case IO_ERROR:
					message = "下载错误";
					break;
				case DECODING_ERROR:
					message = "图片无法显示";
					break;
				case NETWORK_DENIED:
					message = "网络有问题，无法下载";
					break;
				case OUT_OF_MEMORY:
					message = "图片太大无法显示";
					break;
				case UNKNOWN:
					message = "未知的错误";
					break;
				}
				Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
				progressBar.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				progressBar.setVisibility(View.GONE);
				mAttacher.update();
			}
		});
*/
		
		
		
	}

}
