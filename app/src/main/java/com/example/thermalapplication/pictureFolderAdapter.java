import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thermalapplication.R;
import com.example.thermalapplication.imageFolder;
import com.example.thermalapplication.itemClickListener;

import java.util.ArrayList;

public class pictureFolderAdapter {

//    private ArrayList<imageFolder> folders;
//    private Context folderContx;
//    private itemClickListener listenToClick;
//
//    /**
//     *
//     * @param folders An ArrayList of String that represents paths to folders on the external storage that contain pictures
//     * @param folderContx The Activity or fragment Context
//     * @param listen interFace for communication between adapter and fragment or activity
//     */
//    public pictureFolderAdapter(ArrayList<imageFolder> folders, Context folderContx, itemClickListener listen) {
//        this.folders = folders;
//        this.folderContx = folderContx;
//        this.listenToClick = listen;
//    }
//
//    @NonNull
//    @Override
//    public FolderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        View cell = inflater.inflate(R.layout.picture_folder_item, parent, false);
//        return new FolderHolder(cell);
//
//    }
//
//    @NonNull
//    @Override
//    public FolderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        return null;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull FolderHolder holder, int position) {
//        final imageFolder folder = folders.get(position);
//
//        Glide.with(folderContx)
//                .load(folder.getFirstPic())
//                .apply(new RequestOptions().centerCrop())
//                .into(holder.folderPic);
//
//        String text = "("+folder.getNumberOfPics()+") "+folder.getFolderName();
//        holder.folderName.setText(text);
//
//        holder.folderPic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                listenToClick.onPicClicked(folder.getPath(),folder.getFolderName());
//            }
//        });
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return folders.size();
//    }
//
//
//    public class FolderHolder extends RecyclerView.ViewHolder{
//        ImageView folderPic;
//        TextView folderName;
//        CardView folderCard;
//
//        public FolderHolder(@NonNull View itemView) {
//            super(itemView);
//            folderPic = itemView.findViewById(R.id.folderPic);
//            folderName = itemView.findViewById(R.id.folderName);
//            folderCard = itemView.findViewById(R.id.folderCard);
//        }
//    }

}