package com.example.spanishverbtrainer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val green = androidx.compose.ui.graphics.Color(0xFF247A62)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme(colorScheme = lightColorScheme(primary=green, secondary=green)) { App() } }
    }
}

@Composable fun App() {
    var tab by remember { mutableStateOf(0) }
    var sentence by remember { mutableStateOf("Ayer comí sushi.") }
    var selectedVerb by remember { mutableStateOf("comer") }
    var query by remember { mutableStateOf("comer") }
    var showAnswer by remember { mutableStateOf(false) }
    var exercise by remember { mutableIntStateOf(0) }
    var choice by remember { mutableStateOf("") }
    var checked by remember { mutableStateOf(false) }
    val verb = VerbEngine.verbs.firstOrNull { it.infinitive == selectedVerb } ?: VerbEngine.verbs[1]
    val tenseList = VerbEngine.conjugations(verb.infinitive)
    Scaffold(
        topBar = {
            Column(Modifier.fillMaxWidth().padding(18.dp)) {
                Text("🇪🇸 SPANISH VERB TRAINER", style=MaterialTheme.typography.labelLarge, color=green, fontWeight=FontWeight.Bold)
                Text("Грамматика B2", style=MaterialTheme.typography.headlineMedium, fontWeight=FontWeight.Bold)
                Text("Спряжение · перевод · практика", style=MaterialTheme.typography.bodyMedium, color=MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        bottomBar = {
            NavigationBar {
                listOf("Предложение","Спряжение","Тренировка").forEachIndexed { i, label ->
                    NavigationBarItem(selected=tab==i,onClick={tab=i},icon={Text(listOf("Aa","↔","✓")[i])},label={Text(label)})
                }
            }
        }
    ) { padding ->
        when(tab) {
            0 -> Column(Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement=Arrangement.spacedBy(12.dp)) {
                Text("Найти глагол в предложении", style=MaterialTheme.typography.titleLarge, fontWeight=FontWeight.Bold)
                OutlinedTextField(sentence,{sentence=it},modifier=Modifier.fillMaxWidth(),label={Text("Предложение на испанском")},minLines=2)
                Button(onClick={
                    val found=VerbEngine.findVerb(sentence)
                    if(found!=null) selectedVerb=found.infinitive
                    showAnswer=true
                },modifier=Modifier.fillMaxWidth()) { Text("Определить глагол") }
                if(showAnswer) {
                    val found=VerbEngine.findVerb(sentence)
                    Card {
                        Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(6.dp)) {
                            Text(if(found!=null) "Найден глагол" else "Глагол не найден",fontWeight=FontWeight.Bold)
                            Text(found?.infinitive?.uppercase() ?: "—",style=MaterialTheme.typography.headlineMedium,color=green)
                            Text(found?.ru ?: "Попробуйте форму из встроенного словаря.")
                            Text(VerbEngine.ruSentence(sentence,found),style=MaterialTheme.typography.bodySmall)
                            Text("Распознавание работает офлайн по встроенной базе форм.",style=MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                Text("Примеры",style=MaterialTheme.typography.titleMedium,fontWeight=FontWeight.Bold)
                listOf("Yo he comido sushi hoy.","Cuando era niño, vivía en Madrid.","Si tuviera tiempo, viajaría más.").forEach {
                    AssistChip(onClick={sentence=it;showAnswer=false},label={Text(it)})
                }
            }
            1 -> Column(Modifier.padding(padding).padding(horizontal=16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(query,{query=it},modifier=Modifier.fillMaxWidth(),label={Text("Инфинитив глагола")},trailingIcon={
                    TextButton(onClick={
                        val v=VerbEngine.verbs.firstOrNull{it.infinitive.equals(query.trim(),true)}
                        if(v!=null) selectedVerb=v.infinitive
                    }) { Text("Найти") }
                })
                Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    VerbEngine.verbs.take(8).forEach { v ->
                        FilterChip(selected=selectedVerb==v.infinitive,onClick={selectedVerb=v.infinitive;query=v.infinitive},label={Text(v.infinitive)})
                    }
                }
                Text(verb.infinitive.uppercase(),style=MaterialTheme.typography.headlineMedium,color=green,fontWeight=FontWeight.Bold)
                Text(verb.ru + if(verb.type=="irregular") " · неправильный" else " · правильный")
                LazyColumn(verticalArrangement=Arrangement.spacedBy(10.dp),contentPadding=PaddingValues(bottom=12.dp)) {
                    itemsIndexed(tenseList) { _, tense ->
                        Card {
                            Column(Modifier.padding(12.dp),verticalArrangement=Arrangement.spacedBy(5.dp)) {
                                Text(tense.name,fontWeight=FontWeight.Bold,color=green)
                                Text(tense.note,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                                if(tense.forms.size==1) Text(tense.forms[0],fontWeight=FontWeight.SemiBold)
                                else tense.forms.forEachIndexed { i, form ->
                                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                                        Text(VerbEngine.people[i],style=MaterialTheme.typography.bodySmall)
                                        Text(form,fontWeight=FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else -> {
                val questions=listOf(
                    Triple("Вчера я говорил с Марией. Какой Indefinido от HABLAR для YO?","hablé",listOf("hablé","hablaba","he hablado","hablaré")),
                    Triple("Форма Pretérito Perfecto для TÚ от COMER?","has comido",listOf("has comido","habías comido","comiste","hayas comido")),
                    Triple("Gerundio глагола VIVIR?","viviendo",listOf("vivido","viviendo","vivía","vive"))
                )
                val q=questions[exercise%questions.size]
                Column(Modifier.padding(padding).padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
                    Text("Мини-тест",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold)
                    Text("Вопрос ${(exercise%questions.size)+1} из ${questions.size}",color=green)
                    Card { Text(q.first,Modifier.padding(16.dp),style=MaterialTheme.typography.titleMedium) }
                    q.third.forEach { option ->
                        OutlinedButton(onClick={choice=option;checked=false},modifier=Modifier.fillMaxWidth(),colors=ButtonDefaults.outlinedButtonColors(
                            containerColor=if(choice==option) green.copy(alpha=.12f) else MaterialTheme.colorScheme.surface
                        )) { Text(option) }
                    }
                    Button(onClick={checked=true},modifier=Modifier.fillMaxWidth(),enabled=choice.isNotEmpty()) { Text("Проверить") }
                    if(checked) {
                        val ok=choice==q.second
                        Text(if(ok) "Верно! ✓" else "Пока неверно. Правильный ответ: ${q.second}",
                            color=if(ok) green else MaterialTheme.colorScheme.error,fontWeight=FontWeight.Bold)
                        Button(onClick={exercise++;choice="";checked=false},modifier=Modifier.fillMaxWidth()) { Text("Следующий вопрос") }
                    }
                }
            }
        }
    }
}
