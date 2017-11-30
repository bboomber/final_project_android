package my.calendar.myapplication2.Adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import my.calendar.myapplication2.Model.ToDoItem;
import my.calendar.myapplication2.R;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.Holder> {


    private ClickListener listener;
    private List<ToDoItem> toDoItems;

    public MyAdapter(List<ToDoItem> items, ClickListener listener) {
        this.toDoItems = items;
        this.listener = listener;
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);

        return new Holder(view, this.listener);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        ToDoItem todo = this.toDoItems.get(position);
        holder.name.setText(todo.getText());
    }

    @Override
    public int getItemCount() {
        return toDoItems.size();
    }

    class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ClickListener listener;
        public TextView name;

        public Holder(View itemView, ClickListener listener) {
            super(itemView);

            this.listener = listener;
            name = itemView.findViewById(R.id.txtName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            this.listener.onCLick(toDoItems.get(getAdapterPosition()), getAdapterPosition());
        }
    }

    public interface ClickListener {
        void onCLick(ToDoItem item, int position);
    }
}