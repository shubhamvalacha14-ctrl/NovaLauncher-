package net.kdt.pojavlaunch.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    public static final String TAG = "SettingsFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Dynamically looks up layout-land/fragment_settings.xml by its name string
        int layoutId = requireContext().getResources().getIdentifier("fragment_settings", "layout", requireContext().getPackageName());
        return inflater.inflate(layoutId, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Dynamically looks up the close button ID string
        int closeBtnId = requireContext().getResources().getIdentifier("btn_close_settings", "id", requireContext().getPackageName());
        View closeButton = view.findViewById(closeBtnId);
        
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> {
                // Slide out back to the main menu row smoothly
                getParentFragmentManager().popBackStack();
            });
        }
    }
}
