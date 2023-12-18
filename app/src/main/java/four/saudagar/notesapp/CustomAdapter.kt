package four.saudagar.notesapp
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(private val mList: List<ItemsViewModel>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_design, parent, false)

        return ViewHolder(view)
    }
    var onItemClick : ((ItemsViewModel) -> Unit)? = null
    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]

        holder.cardThumb.setImageResource(ItemsViewModel.image)
        holder.cardTitle.text = ItemsViewModel.title
        holder.cardContent.text = ItemsViewModel.text
        holder.itemView.setOnClickListener{
            onItemClick?.invoke(ItemsViewModel)
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val card: CardView = itemView.findViewById(R.id.card)
        val cardThumb: ImageView = itemView.findViewById(R.id.cardThumb)
        val cardTitle: TextView = itemView.findViewById(R.id.cardTitle)
        val cardContent: TextView = itemView.findViewById(R.id.cardContent)
    }
}
