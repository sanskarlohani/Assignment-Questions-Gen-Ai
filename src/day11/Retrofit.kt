package day11

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.sanskar.compose_basics.ui.theme.Compose_BasicsTheme
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            Compose_BasicsTheme {
                ProductScreen()
            }
        }
    }
}

//Data Class
data class ProductsItem(
    val category: String,
    val description: String,
    val id: Int,
    val image: String,
    val price: Double,
    val rating: Rating,
    val title: String
)

data class Rating(
    val count: Int,
    val rate: Double
)


//interface
interface ProductAPIService {
    @GET("products")
    suspend fun getProducts(): List<ProductsItem>
}


//retrofitInstance
object RetrofitInstance {
    private const val BASE_URL = "https://fakestoreapi.com/"

    val api: ProductAPIService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductAPIService::class.java)
    }
}



//viewModel
class ProductViewModel : ViewModel() {

    private val _productList = MutableLiveData<List<ProductsItem>>()
    val productList: LiveData<List<ProductsItem>> = _productList

    var errorMessage by mutableStateOf("")

    fun fetchProducts() {
        viewModelScope.launch {
            try {
                val products = RetrofitInstance.api.getProducts()
                _productList.value = products
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Unknown error"
            }
        }
    }
}



//UI Screen
@Composable
fun ProductScreen(viewModel: ProductViewModel = viewModel()) {
    val products by viewModel.productList.observeAsState(emptyList())
    val errorMessage = viewModel.errorMessage

    LaunchedEffect(Unit) {
        viewModel.fetchProducts()
    }

    when {
        errorMessage.isNotEmpty() -> {
            Text(
                text = "Error: $errorMessage",
                modifier = Modifier.padding(16.dp),
                color = Color.Red
            )
        }

        products.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(products.size) { index ->
                    val product = products[index]
                    ProductCard(product)
                }
            }
        }
    }
}



@Composable
fun ProductCard(product: ProductsItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AsyncImage(
                model = product.image,
                contentDescription = product.title,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = product.title,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "₹${product.price}",
                    color = Color.Gray
                )
                Text(
                    text = product.category,
                    color = Color.Gray
                )
                Text(
                    text = product.description,
                    maxLines = 5
                )
                Text(
                    text = "Rating: ${product.rating.rate}",
                    color = Color.Gray
                )
                Text(
                    text = "Count: ${product.rating.count}",
                    color = Color.Gray
                )
            }
        }
    }
}