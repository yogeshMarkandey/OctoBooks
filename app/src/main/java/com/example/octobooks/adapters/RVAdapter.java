package com.example.octobooks.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.octobooks.R;
import com.example.octobooks.data.Books;

public class RVAdapter extends ListAdapter<Books, RVAdapter.BooksViewHolder> {


    // Constructor
    public RVAdapter() {
        super(callback);
    }


    // Diff.Util CallBack
    private static DiffUtil.ItemCallback<Books> callback = new DiffUtil.ItemCallback<Books>() {
        @Override
        public boolean areItemsTheSame(@NonNull Books oldItem, @NonNull Books newItem) {
            return oldItem.getBook_id().equals(newItem.getBook_id());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Books oldItem, @NonNull Books newItem) {
            return oldItem.getSubject().equals(newItem.getSubject()) &&
                    oldItem.getMedium().equals(newItem.getMedium()) &&
                    oldItem.getEdition().equals(newItem.getEdition());
        }
    };




    @NonNull
    @Override
    public BooksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View  v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv, parent, false);
        BooksViewHolder holder = new BooksViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BooksViewHolder holder, int position) {
        holder.bindData(position);
    }

    public class BooksViewHolder extends RecyclerView.ViewHolder  {

        private TextView bookName, bookClass, bookSubject, medium, questions;
        private ImageView imageView;

        public BooksViewHolder(@NonNull View itemView) {
            super(itemView);

            bookName = itemView.findViewById(R.id.textView_book_name);
            bookClass = itemView.findViewById(R.id.textView_class);
            bookSubject = itemView.findViewById(R.id.textView_subject);
            imageView = itemView.findViewById(R.id.imageView_book);
            medium = itemView.findViewById(R.id.textView_medium);
            questions = itemView.findViewById(R.id.textView_question);

        }


        private void bindData(int position){
            Books book = getItem(position);
            bookName.setText(book.getName());
            bookClass.setText("Class :   " + book.getClass_name());
            bookSubject.setText("Subject :   " + book.getSubject());
            medium.setText("Medium :   " + book.getMedium());
            questions.setText("Number of Questions :   " + book.getQuestion_count());

            Glide.with(itemView)
                    .load(book.getPhoto_url())
                    .error(R.drawable.back)
                    .placeholder(R.drawable.back)
                    .into(imageView);

        }
    }
}
