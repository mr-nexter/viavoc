package com.viavoc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class WordsAdapter extends RecyclerView.Adapter<WordsAdapter.Holder> {

    private Context context;
    private List<String> words;
    private WordOnClickListener wordsListInteractionListener;

    public WordsAdapter(Context context, List<String> words, WordOnClickListener wordsListInteractionListener) {
        this.context = context;
        this.words = words;
        this.wordsListInteractionListener = wordsListInteractionListener;


        if (words != null) {
            System.out.println("words size inside adapter: " + words.size());
        }
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.word_layout, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder viewHolder, int i) {
        final int position = viewHolder.getAdapterPosition();
        viewHolder.wordText.setText(words.get(position));
        viewHolder.wordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wordsListInteractionListener.changeWord(viewHolder.getAdapterPosition());
            }
        });

        viewHolder.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                words.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        public LinearLayout container;
        public ImageButton closeButton;
        public TextView wordText;

        public Holder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.word_container);

            closeButton = itemView.findViewById(R.id.word_close);
            wordText = itemView.findViewById(R.id.word_text);
        }
    }

    public interface WordOnClickListener {
        public void changeWord(int position);
    }
}
