package ru.devxem.reminder.ui.notifications;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Objects;

import ru.devxem.reminder.MainActivity;
import ru.devxem.reminder.R;
import ru.devxem.reminder.api.Error;
import ru.devxem.reminder.api.GetNotes;

public class NotificationsFragment extends Fragment {
    private static RecyclerView rv;
    @SuppressLint("StaticFieldLeak")
    private static SwipeRefreshLayout swipeRefreshLayout;

    public static void Update(ArrayList<ArrayList<String>> data) {
        RVAdapter adapter = new RVAdapter(data);
        rv.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        final Context context = getContext();
        try {
            // findViewById() делать через root. !
            swipeRefreshLayout = root.findViewById(R.id.swipe_notes);

            rv = root.findViewById(R.id.recycler_notifications);
            rv.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(context);
            rv.setLayoutManager(llm);
            swipeRefreshLayout.setRefreshing(true);
            GetNotes.updateNotes(Objects.requireNonNull(context), MainActivity.getSss().get(1), 0);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    GetNotes.updateNotes(Objects.requireNonNull(context), MainActivity.getSss().get(1), 1);
                }
            });
        } catch (Exception e) {
            Error.setErr(context, e.toString(), Objects.requireNonNull(context).getSharedPreferences("settings", Context.MODE_PRIVATE).getString("email", null));
        }
        return root;
    }
}

class RVAdapter extends RecyclerView.Adapter<RVAdapter.NotesViewHolder> {
    private ArrayList<String> ids;
    private ArrayList<String> date;
    private ArrayList<String> head;
    private ArrayList<String> text;

    RVAdapter(ArrayList<ArrayList<String>> data) {
        ids = data.get(0);
        date = data.get(1);
        head = data.get(2);
        text = data.get(3);
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        return new NotesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        //holder.id_text.setText(ids.get(position));
        holder.date_text.setText(date.get(position));
        holder.head_text.setText(head.get(position));
        holder.body_text.setText(text.get(position));
    }

    @Override
    public int getItemCount() {
        return ids.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    static class NotesViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView id_text;
        TextView head_text;
        TextView body_text;
        TextView date_text;

        NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            id_text = itemView.findViewById(R.id.id_text);
            head_text = itemView.findViewById(R.id.head_text);
            body_text = itemView.findViewById(R.id.body_text);
            date_text = itemView.findViewById(R.id.time_text);
        }
    }

}
