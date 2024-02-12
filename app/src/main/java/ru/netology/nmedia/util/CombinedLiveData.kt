import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import ru.netology.nmedia.dto.Post

//https://stackoverflow.com/questions/49493772/mediatorlivedata-or-switchmap-transformation-with-multiple-parameters
class CombinedLiveData(a: LiveData<List<Post>>, b: LiveData<List<Post>>) : MediatorLiveData<List<Post>>() {
    init {
        addSource(a) { first ->
            value = (first ?: emptyList<Post>())
            addSource(b) { second ->
                value = (second ?: emptyList<Post>())
            }
        }
    }
}

class CombinedLiveData2(source1: LiveData<List<Post>>, source2: LiveData<List<Post>>, private val combine: (data1: List<Post>, data2: List<Post>) -> List<Post>) : MediatorLiveData<List<Post>>() {

    private var data1: List<Post> = emptyList()
    private var data2: List<Post> = emptyList()

    init {
        super.addSource(source1) {
            data1 = it
            value = combine(data1, data2)
        }
        super.addSource(source2) {
            data2 = it
            value = combine(data1, data2)
        }
    }
}
