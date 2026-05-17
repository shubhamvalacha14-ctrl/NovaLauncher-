package net.kdt.pojavlaunch.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import net.kdt.pojavlaunch.instances.Instance;
import net.kdt.pojavlaunch.instances.Instances;
import java.util.ArrayList;
import java.util.List;

public class VersionListFragment extends Fragment {

    public static final String TAG = "VersionListFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutId = requireContext().getResources().getIdentifier("fragment_version_list", "layout", requireContext().getPackageName());
        return inflater.inflate(layoutId, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Back button navigation handler
        int backBtnId = requireContext().getResources().getIdentifier("btn_back_versions", "id", requireContext().getPackageName());
        View backButton = view.findViewById(backBtnId);
        if (backButton != null) {
            backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }

        // Target our dynamic ListView container
        int listId = requireContext().getResources().getIdentifier("version_list_render", "id", requireContext().getPackageName());
        ListView listView = view.findViewById(listId);

        if (listView != null) {
            // Grab actual launcher versions from storage backend dynamically
            List<Instance> installedInstances = Instances.getInstancesList();
            List<String> versionNames = new ArrayList<>();
            
            if (installedInstances != null && !installedInstances.isEmpty()) {
                for (Instance instance : installedInstances) {
                    versionNames.add(instance.getName());
                }
            } else {
                versionNames.add("No versions found - Please install one!");
            }

            // Bind the versions list items to the screen view row layout template
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, versionNames) {
                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    View row = super.getView(position, convertView, parent);
                    TextView text = row.findViewById(android.R.id.text1);
                    if (text != null) {
                        text.setTextColor(android.graphics.Color.WHITE);
                        text.setTextSize(13);
                        text.setPadding(16, 16, 16, 16);
                    }
                    row.setBackgroundResource(requireContext().getResources().getIdentifier("rounded_card_bg", "drawable", requireContext().getPackageName()));
                    row.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#1C1D21")));
                    return row;
                }
            };

            listView.setAdapter(adapter);

            // Handle when a version is tapped
            listView.setOnItemClickListener((parent1, view1, position, id) -> {
                if (installedInstances != null && position < installedInstances.size()) {
                    // Update active launcher choice selection globally
                    Instances.selectInstance(installedInstances.get(position));
                }
                getParentFragmentManager().popBackStack();
            });
        }
    }
}
