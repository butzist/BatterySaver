package de.szalkowski.adamsbatterysaver.ui;

import java.util.LinkedList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import de.szalkowski.adamsbatterysaver.AdamsBatterySaverApplication;
import de.szalkowski.adamsbatterysaver.R;
import de.szalkowski.adamsbatterysaver.devices.PowerSaver;

public class DeviceMatrixSectionFragment extends Fragment {
    private View rootView;
    private TableLayout deviceMatrix;
    private List<DeviceMatrixRow> rows;
    
    public static interface DeviceMatrixRow {
    	public View getView();
    	public void update();
    	public void save();
    }
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
    	rootView = inflater.inflate(R.layout.device_matrix_fragment, container, false);		

		setupRows();
    	loadSettings();
		setupWidgets();
        
		return rootView;
    }
    
    @Override
    public void onDestroyView() {
    	saveSettings();
    	super.onDestroyView();
    }

	private void setupRows() {
		rows = new LinkedList<DeviceMatrixRow>();
		for(PowerSaver powersaver : AdamsBatterySaverApplication.getPowerSavers()) {
			rows.add(new DeviceMatrixRowPowerSaverAdapter(powersaver, getActivity()));									
		}
	}
	private void setupWidgets() {
		deviceMatrix = (TableLayout)rootView.findViewById(R.id.TableViewDeviceMatrix);
		for(DeviceMatrixRow row : rows) {
			deviceMatrix.addView(row.getView());									
		}
	}

	private void saveSettings() {
		AdamsBatterySaverApplication.getSettings().startTransaction();
		for(DeviceMatrixRow row : rows) {
			row.save();									
		}
		AdamsBatterySaverApplication.getSettings().commitTransaction();
	}
	
	private void loadSettings() {
		for(DeviceMatrixRow row : rows) {
			row.update();									
		}
	}
}
