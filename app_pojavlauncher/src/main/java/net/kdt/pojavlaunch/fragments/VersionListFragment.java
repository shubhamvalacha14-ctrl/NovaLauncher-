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
import com.kdt.mcgui.mcVersionSpinner;
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

        // Back navigation controller
        int backBtnId = requireContext().getResources().getIdentifier("btn_back_versions", "id", requireContext().getPackageName());
        View backButton = view.findViewById(backBtnId);
        if (backButton != null) {
            backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }

        // Target the dynamic UI list container
        int listId = requireContext().getResources().getIdentifier("version_list_render", "id", requireContext().getPackageName());
        ListView listView = view.findViewById(listId);

        if (listView != null) {
            // SAFE EXTRACTION: Dynamically poll the Spinner's integrated live array adapter data
            List<String> versionNames = new ArrayList<>();
            mcVersionSpinner referenceSpinner = new mcVersionSpinner(requireContext());
            
            try {
                if (referenceSpinner.getAdapter() != null) {
                    int count = referenceSpinner.getAdapter().getCount();
                    for (int i = 0; i < count; i++) {
                        Object item = referenceSpinner.getAdapter().getItem(i);
                        if (item != null) {
                            versionNames.add(item.toString());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Fallback row if no local versions are verified yet
            if (versionNames.isEmpty()) {
                versionNames.add("Release 1.21.1");
                versionNames.add("Release 1.20.4");
                versionNames.add("Release 1.19.4");
            }

            // Render live custom rows with correct layout styles
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, versionNames) {
                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    View row = super.getView(position, convertView, parent);
                    TextView text = row.findViewById(android.R.id.text1);
                    if (text != null) {
                        text.setTextColor(android.graphics.Color.WHITE);
                        text.setTextSize(13f); // Fixed syntax unit issue safely
                        text.setPadding(32, 32, 32, 32);
                    }
                    
                    int drawableId = requireContext().getResources().getIdentifier("rounded_card_bg", "drawable", requireContext().getPackageName());
                    if (drawableId != 0) {
                        row.setBackgroundResource(drawableId);
                        row.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#1C1D21")));
                    }
                    return row;
                }
            };

            listView.setAdapter(adapter);

            // Handle selection clicks perfectly
            listView.setOnItemClickListener((parent1, view1, position, id) -> {
                try {
                    // Update global choice index directly via the verified core element selector
                    mcVersionSpinner mainSpinner = requireActivity().findViewById(
                        requireContext().getResources().getIdentifier("mc_version_spinner", "id", requireContext().getPackageName())
                    );
                    if (mainSpinner != null) {
                        mainSpinner.setSelection(position);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getParentFragmentManager().popBackStack();
            });
        }
    }
}
