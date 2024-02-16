import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import ru.netology.nmedia.dto.Post

//https://gist.github.com/guness/0a96d80bc1fb969fa70a5448aa34c215
class CombinedLiveData(
    source1: LiveData<List<Post>>,
    source2: LiveData<List<Post>>,
    private val combine: (data1: List<Post>, data2: List<Post>) -> List<Post>
) : MediatorLiveData<List<Post>>() {

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

fun CombinedFlow(
    flow1: Flow<List<Post>>,
    flow2: Flow<List<Post>>,
    transform: (data1: List<Post>, data2: List<Post>) -> List<Post>,
): Flow<List<Post>> = combine(flow1, flow2) { list1, list2 ->
    transform(list1, list2)
}

