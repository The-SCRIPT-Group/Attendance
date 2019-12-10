package `in`.thescriptgroup.attendance

import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerItemClickListener(
    private val mRecycler: RecyclerView,
    private val clickListener: ((position: Int, view: View) -> Unit)? = null,
    private val longClickListener: ((position: Int, view: View) -> Unit)? = null
) : RecyclerView.OnChildAttachStateChangeListener {

    override fun onChildViewDetachedFromWindow(view: View) {
        view.setOnClickListener(null)
        view.setOnLongClickListener(null)
    }

    override fun onChildViewAttachedToWindow(view: View) {
        view.setOnClickListener { v -> setOnChildAttachedToWindow(v) }
    }

    private fun setOnChildAttachedToWindow(v: View?) {
        if (v != null) {
            val position = mRecycler.getChildLayoutPosition(v)
            if (position >= 0) {
                clickListener?.invoke(position, v)
                longClickListener?.invoke(position, v)
            }
        }
    }
}

@JvmOverloads
fun RecyclerView.affectOnItemClicks(
    onClick: ((position: Int, view: View) -> Unit)? = null,
    onLongClick: ((position: Int, view: View) -> Unit)? = null
) {
    this.addOnChildAttachStateChangeListener(RecyclerItemClickListener(this, onClick, onLongClick))
}