# Tech doc

## documentation

all code that you send must be with documentation on Russian with @param, @property

- class doc example:

```kotlin 
/**
 * Представляет пользователя в системе.
 *
 * @property id Уникальный идентификатор пользователя
 * @property name Полное имя пользователя
 * @property email Email адрес пользователя
 * @property age Возраст пользователя в годах, или null если не указан
 */
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val age: Int?
)
```

- class with body doc example

```kotlin
/**
 * ViewModel для экрана "Избранное", управляющий списком любимых персонажей
 * и связанными пользовательскими действиями, включая обновление и навигацию.
 *
 * @property characterLocalRepository Репозиторий для доступа к локальным данным персонажей.
 * @property favouritesLocalRepository Репозиторий для управления избранными персонажами.
 * @property settingsManager Менеджер для состояния настроек приложения.
 * @property sendRootIntent Функция для отправки навигационных интентов в корневой обработчик навигации.
 */
@HiltViewModel(assistedFactory = FavouritesViewModel.Factory::class)
internal class FavouritesViewModel @AssistedInject constructor(
    private val characterLocalRepository: CharacterLocalRepository,
    private val favouritesLocalRepository: FavouritesLocalRepository,
    private val settingsManager: SettingsManager,
    @Assisted private val sendRootIntent: (NavRootIntent) -> Unit,
) : ViewModel() {
    private val settingsFlow = settingsManager.settingsFlow

    /**
     * Flow постраничных данных избранных персонажей.
     * Наблюдает за списком ID избранных элементов и преобразует их в постраничный flow данных персонажей.
     * Результат кэшируется в рамках [viewModelScope].
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val favouriteCharactersFlow =
        favouritesLocalRepository.getAllFavourites().flatMapLatest { favourites ->
            favourites.toPagingFlow()
        }.cachedIn(viewModelScope)
}
```

- fun doc example:

```kotlin 
/**
 * Вычисляет скидку на товар в зависимости от статуса пользователя.
 *
 * @param product Товар для расчета скидки
 * @param user Пользователь, запрашивающий скидку
 * @param isPremiumMember Является ли пользователь премиум-участником
 * @return Процент скидки (0-100)
 * @throws IllegalArgumentException если цена товара отрицательная
 */
fun calculateDiscount(
    product: Product,
    user: User,
    isPremiumMember: Boolean
): Int {
    require(product.price >= 0) { "Цена товара не может быть отрицательной" }
    
    // Логика расчета
    return when {
        isPremiumMember -> 20
        user.age != null && user.age < 25 -> 15
        else -> 10
    }
}
```

- one-line fun doc example:

```kotlin 
/**
 * Форматирует имя пользователя для отображения.
 *
 * @param firstName Имя пользователя
 * @param lastName Фамилия пользователя
 * @param capitalize Нужно ли делать первую букву заглавной (по умолчанию true)
 * @return Отформатированное полное имя
 */
fun formatUserName(
    firstName: String,
    lastName: String,
    capitalize: Boolean = true
): String = if (capitalize) {
        "${firstName.capitalize()} ${lastName.capitalize()}"
    } else {
        "$firstName $lastName"
    }
```

## common rules

- use oneline functions where possible
- all classes are internal in data module
- all interfaces are public in domain module
- no import *, only single import
- no docs for files with di 