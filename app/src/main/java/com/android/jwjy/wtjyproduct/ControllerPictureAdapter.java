package com.android.jwjy.wtjyproduct;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HaiyuKing
 * Used 照片网格适配器
 */

public class ControllerPictureAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

	private static final String TAG = ControllerPictureAdapter.class.getSimpleName();

	/**上下文*/
	private Context myContext;
	/**自定义列表项标题集合*/
	private ArrayList<ControllerPictureBean> listitemList;

	final static int TYPE_ADD = 1;
	final static int TYPE_PHOTO = 2;

	public final static int MAX = 9;//总数目，这里根据实际情况设置，设置100基本上表明无限制了

	/*
	* 构造函数
	*/
	public ControllerPictureAdapter(Context context, ArrayList<ControllerPictureBean> itemlist) {
		myContext = context;
		listitemList = itemlist;
	}

	/**
	 * 获取总的条目数
	 */
	@Override
	public int getItemCount() {
		Log.w(TAG,"{getItemCount}listitemList.size()="+listitemList.size());
		int count = listitemList.size();
		if (count > MAX) {
			count = MAX;
		}
		count = count + 1;
		return count;
	}

	@Override
	public int getItemViewType(int position) {
		Log.w(TAG,"{getItemViewType}position="+position);
		Log.w(TAG,"{getItemViewType}listitemList.size()="+listitemList.size());
		return (position == listitemList.size()) ? TYPE_ADD : TYPE_PHOTO;
	}

	/**
	 * 创建ViewHolder
	 */
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if(viewType == TYPE_ADD) {
			View viewfoot = LayoutInflater.from(myContext).inflate(R.layout.pic_grid_foot_item, parent, false);
			ItemFootViewHolder itemfootViewHolder = new ItemFootViewHolder(viewfoot);
			return itemfootViewHolder;
		} else if(viewType == TYPE_PHOTO) {
			View view = LayoutInflater.from(myContext).inflate(R.layout.pic_grid_item, parent, false);
			ItemViewHolder itemViewHolder = new ItemViewHolder(view);
			return itemViewHolder;
		}
		return null;
	}

	/**
	 * 声明列表项ViewHolder*/
	static class ItemViewHolder extends RecyclerView.ViewHolder
	{
		public ItemViewHolder(View view)
		{
			super(view);
			griditemLayout = view.findViewById(R.id.griditemLayout);
//			griditemimgLayout = view.findViewById(R.id.griditemimgLayout);
			grid_img = view.findViewById(R.id.grid_img);
//			grid_img_state = view.findViewById(R.id.grid_img_state);
			imageView_delete = view.findViewById(R.id.imageView_delete);
		}

		private LinearLayout griditemLayout;
//		RelativeLayout griditemimgLayout;
		private ImageView grid_img;
		private ImageView imageView_delete;

//		TextView grid_img_state;
	}

	/**
	 * 声明最后一个ViewHolder*/
	static class ItemFootViewHolder extends RecyclerView.ViewHolder
	{
		public ItemFootViewHolder(View view)
		{
			super(view);
			gridfootitemLayout = view.findViewById(R.id.gridfootitemLayout);
		}
		RelativeLayout gridfootitemLayout;
	}

	/**
	 * 将数据绑定至ViewHolder
	 */
	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int index) {

		if(viewHolder instanceof ItemViewHolder){
			ControllerPictureBean listItemModel = listitemList.get(index);
			final ItemViewHolder itemViewHold = ((ItemViewHolder)viewHolder);

			Uri uri = Uri.fromFile(new File(listItemModel.getPicPath()));

			Glide.with(myContext)
					.load(uri)
					//设置等待时的图片
					.placeholder(R.drawable.img_loading)
					//设置加载失败后的图片显示
					.error(R.drawable.img_error)
//					//默认淡入淡出动画
//					.crossFade()
					//缓存策略,跳过内存缓存【此处应该设置为false，否则列表刷新时会闪一下】
					.skipMemoryCache(false)
					//缓存策略,硬盘缓存-仅仅缓存最终的图像，即降低分辨率后的（或者是转换后的）
					.diskCacheStrategy(DiskCacheStrategy.ALL)
					//设置图片加载的优先级
					.priority(Priority.HIGH)
					.into(itemViewHold.grid_img);
			itemViewHold.imageView_delete.setClickable(true);
//			itemViewHold.grid_img_state.setText("(" + (index + 1) + "/" + listitemList.size() + ")");

			//如果设置了回调，则设置点击事件
			if (mOnItemClickLitener != null)
			{
				itemViewHold.grid_img.setOnClickListener(view ->{
						int position = itemViewHold.getLayoutPosition();//在增加数据或者减少数据时候，position和index就不一样了
						mOnItemClickLitener.onItemClick(view,position);
					});
				itemViewHold.imageView_delete.setOnClickListener(view->{
					//删除图片
					int position = itemViewHold.getLayoutPosition();//在增加数据或者减少数据时候，position和index就不一样了
					mOnItemClickLitener.onItemDeleteClick(view,position);
				});
			}
		}else if(viewHolder instanceof ItemFootViewHolder){
			final ItemFootViewHolder itemFootViewHold = ((ItemFootViewHolder)viewHolder);
			//如果设置了回调，则设置点击事件
			if (mOnItemClickLitener != null)
			{
				itemFootViewHold.gridfootitemLayout.setOnClickListener(view-> {
						mOnItemClickLitener.onItemAddClick();
					});
			}
		}

	}

	/**
	 * 添加Item--用于动画的展现*/
	public void addItem(int position,ControllerPictureBean itemModel) {
		listitemList.add(position,itemModel);
		notifyItemInserted(position);
	}
	/**
	 * 删除Item--用于动画的展现*/
	public void removeItem(int position) {
		listitemList.remove(position);
		notifyItemRemoved(position);
	}

	/*=====================添加OnItemClickListener回调================================*/
	public interface OnItemClickLitener
	{
		/**图片的点击事件*/
		void onItemClick(View view, int position);
		/**添加的点击事件*/
		void onItemAddClick();
		/**删除的点击事件*/
		void onItemDeleteClick(View view, int position);
	}

	private OnItemClickLitener mOnItemClickLitener;

	public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
	{
		this.mOnItemClickLitener = mOnItemClickLitener;
	}

	//返回当前图片集合的所有路径集合【用于预览】
	public List<String> getAllPhotoPaths() {
		List<String> allPhotoPaths = new ArrayList<String>(listitemList.size());
		for (ControllerPictureBean  pictureBean: listitemList) {
			allPhotoPaths.add(pictureBean.getPicPath());
		}
		return allPhotoPaths;
	}

}
