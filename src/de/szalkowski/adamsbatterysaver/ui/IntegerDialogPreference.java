package de.szalkowski.adamsbatterysaver.ui;

import de.szalkowski.adamsbatterysaver.R;
import de.szalkowski.adamsbatterysaver.R.id;
import de.szalkowski.adamsbatterysaver.R.layout;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

public class IntegerDialogPreference extends DialogPreference {
	private View picker;
	private int value;
	
	public IntegerDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		if(!hasDialogLayout(attrs)) {
			setDialogLayoutResource(R.layout.number_picker_dialog);
		}
	}

	public IntegerDialogPreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);

		if(!hasDialogLayout(attrs)) {
			setDialogLayoutResource(R.layout.number_picker_dialog);
		}
	}
	
	private boolean hasDialogLayout(AttributeSet attrs) {
		String value = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "dialogLayout");
		return (value != null);
	}

	@SuppressLint("NewApi")
	protected void setPicker(int value) {
		if(android.os.Build.VERSION.SDK_INT >= 11) {
			NumberPicker picker = (NumberPicker)this.picker;
			picker.setMinValue(1);
			picker.setMaxValue(1000000000);
			picker.setValue(value);
		} else {
			EditText picker = (EditText)this.picker;
			picker.setText(Integer.toString(value));
		}
		
	}
	
	@SuppressLint("NewApi")
	protected int getPicker() {
		if(android.os.Build.VERSION.SDK_INT >= 11) {
			NumberPicker picker = (NumberPicker)this.picker;
			return picker.getValue();
		} else {
			EditText picker = (EditText)this.picker;
			return Integer.parseInt(picker.getText().toString());
		}
	}
	
	@Override
	protected void onBindDialogView(View view) {
		this.picker = view.findViewById(R.id.pref_number_picker);
		this.setPicker(this.value);
		
		super.onBindDialogView(view);
	}

	@Override
	public CharSequence getSummary() {
		return String.format(super.getSummary().toString(), this.value);
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		super.onClick(dialog, which);
		
		if(which == DialogInterface.BUTTON_POSITIVE) {
			this.value = getPicker();
			if(shouldPersist()) {
				this.persistInt(this.value);
			}
			this.notifyChanged();
		}
	}
	
	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getInt(index, 0);
	}
	
	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
		try {
			this.value = (Integer)defaultValue;
		}
		catch(Exception e1) {
			try {
				this.value = Integer.parseInt(defaultValue.toString());
			}
			catch(Exception e2) {
				this.value = 1;
			}
		}
		
		if(restorePersistedValue) {
			this.value = getPersistedInt(this.value);
		}
		
		if(this.shouldPersist()) {
			this.persistInt(this.value);
		}
	}
	
	
	
	
	// Everything form here to bottom is from Android Docs
	
	@Override
	protected Parcelable onSaveInstanceState() {
	    final Parcelable superState = super.onSaveInstanceState();
	    // Check whether this Preference is persistent (continually saved)
	    if (isPersistent()) {
	        // No need to save instance state since it's persistent, use superclass state
	        return superState;
	    }

	    // Create instance of custom BaseSavedState
	    final SavedState myState = new SavedState(superState);
	    // Set the state's value with the class member that holds current setting value
	    myState.value = this.getPicker();
	    return myState;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
	    // Check whether we saved the state in onSaveInstanceState
	    if (state == null || !state.getClass().equals(SavedState.class)) {
	        // Didn't save the state, so call superclass
	        super.onRestoreInstanceState(state);
	        return;
	    }

	    // Cast state to custom BaseSavedState and pass to superclass
	    SavedState myState = (SavedState) state;
	    super.onRestoreInstanceState(myState.getSuperState());
	    
	    // Set this Preference's widget to reflect the restored state
	    this.setPicker(myState.value);
	}	
	
	private static class SavedState extends BaseSavedState {
	    // Member that holds the setting's value
	    // Change this data type to match the type saved by your Preference
	    int value;

	    public SavedState(Parcelable superState) {
	        super(superState);
	    }

	    public SavedState(Parcel source) {
	        super(source);
	        // Get the current preference's value
	        value = source.readInt();  // Change this to read the appropriate data type
	    }

	    @Override
	    public void writeToParcel(Parcel dest, int flags) {
	        super.writeToParcel(dest, flags);
	        // Write the preference's value
	        dest.writeInt(value);  // Change this to write the appropriate data type
	    }

	    // Standard creator object using an instance of this class
	    @SuppressWarnings("unused")
		public static final Parcelable.Creator<SavedState> CREATOR =
	            new Parcelable.Creator<SavedState>() {

	        public SavedState createFromParcel(Parcel in) {
	            return new SavedState(in);
	        }

	        public SavedState[] newArray(int size) {
	            return new SavedState[size];
	        }
	    };
	}
}
