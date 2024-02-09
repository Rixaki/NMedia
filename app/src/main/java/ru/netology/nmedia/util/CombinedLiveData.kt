import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import ru.netology.nmedia.dto.Post

//https://stackoverflow.com/questions/49493772/mediatorlivedata-or-switchmap-transformation-with-multiple-parameters
class CombinedLiveData(a: LiveData<List<Post>>, b: LiveData<List<Post>>) : MediatorLiveData<Pair<List<Post>, List<Post>>>() {
    init {
        addSource(a) { value = (it ?: emptyList<Post>()) to (b.value ?: emptyList<Post>()) }
        addSource(b) { value = (a.value ?: emptyList<Post>()) to (it ?: emptyList<Post>()) }
    }
}