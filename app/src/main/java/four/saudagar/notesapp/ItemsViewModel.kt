package four.saudagar.notesapp

import android.os.Parcel
import android.os.Parcelable


data class ItemsViewModel(val id: String, val image: Int, val title: String, val text: String, val start: Long, val end: Long, val date: Long) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeInt(image)
        parcel.writeString(title)
        parcel.writeString(text)
        parcel.writeLong(start)
        parcel.writeLong(end)
        parcel.writeLong(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemsViewModel> {
        override fun createFromParcel(parcel: Parcel): ItemsViewModel {
            return ItemsViewModel(parcel)
        }

        override fun newArray(size: Int): Array<ItemsViewModel?> {
            return arrayOfNulls(size)
        }
    }
}
