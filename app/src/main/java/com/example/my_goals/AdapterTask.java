package com.example.my_goals;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_goals.models.Task;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AdapterTask extends  RecyclerView.Adapter<AdapterTask.Holder>{

    ArrayList<Task> list;
    Holder.TaskInterface listener;

    public AdapterTask(ArrayList<Task> list, Holder.TaskInterface _listener) {
        this.list = list;
        listener = _listener;
    }

    public void addTask(Task task) {
        this.list.add(task);
        notifyItemInserted(list.size() - 1); // Updating adapter list
    }

    @NonNull
    @NotNull
    @Override
    public Holder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_card, parent, false);
        Holder holder = new Holder(v, listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull Holder holder, int position) {
        holder.tv_title.setText(list.get(position).getName().toUpperCase());

        holder.tv_repetition.setText("Frequency: " +  list.get(position).getRepetition()
         + ", " + list.get(position).getTime());
        //set image depend on task is done for today or not
        if(list.get(position).isDone_today()){

            holder.im_task_done.setVisibility(View.VISIBLE);
            holder.im_checkbox.setVisibility(View.INVISIBLE);
        }
        else if(!list.get(position).isDone_today()){
            holder.im_task_done.setVisibility(View.INVISIBLE);
            holder.im_checkbox.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv,im_checkbox, im_task_done;;
        TextView tv_title,  tv_repetition;
        TaskInterface listener;


        public Holder(@NonNull @NotNull View itemView, TaskInterface _listener) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_card_task_title);
            tv_repetition = itemView.findViewById(R.id.tv_card_task_repetition);
            im_checkbox = itemView.findViewById(R.id.im_task_card_check_box_done);
            im_task_done = itemView.findViewById(R.id.im_task_card_done);
            im_checkbox.setVisibility(View.VISIBLE);

            listener = _listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onTaskClick(getAdapterPosition());
        }

        public interface TaskInterface {
            public void onTaskClick(int i);



        }
    }
}


