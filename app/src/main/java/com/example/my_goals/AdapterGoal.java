package com.example.my_goals;

        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.ProgressBar;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;


        import com.example.my_goals.models.Goal;
        import com.squareup.picasso.Picasso;

        import org.jetbrains.annotations.NotNull;

        import java.util.ArrayList;

public class AdapterGoal extends RecyclerView.Adapter<AdapterGoal.Holder> {

    ArrayList<Goal> list;
    Holder.GoalInterface listener;

    public AdapterGoal(ArrayList<Goal> list, Holder.GoalInterface _listener) {
        this.list = list;
        listener = _listener;
    }

    @NonNull
    @NotNull
    @Override
    public Holder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.goal_card, parent, false);
        Holder holder = new Holder(v, listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull Holder holder, int position) {
        holder.tv_title.setText(list.get(position).getTitle());
        holder.progressBarGoal.setProgress(80);
        holder.tv_type.setText("Type: " + list.get(position).getCategory());
        Picasso.get().load(list.get(position).getUrl_image()).fit().into(holder.iv);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv;
        TextView tv_title, ed_description, tv_type;
        GoalInterface listener;
        ProgressBar progressBarGoal;


        public Holder(@NonNull @NotNull View itemView, GoalInterface _listener) {
            super(itemView);
            iv = itemView.findViewById(R.id.img_card_goal);
            tv_title = itemView.findViewById(R.id.tv_card_goal_title_goal);
            tv_type = itemView.findViewById(R.id.tv_card_goal_type_goal);



            listener = _listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onGoalClick(getAdapterPosition());
        }

        public interface GoalInterface {
            public void onGoalClick(int i);
        }
    }
}

