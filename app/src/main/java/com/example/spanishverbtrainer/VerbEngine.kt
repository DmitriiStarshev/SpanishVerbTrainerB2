package com.example.spanishverbtrainer

data class Verb(val infinitive: String, val ru: String, val type: String = "regular")
data class Tense(val name: String, val forms: List<String>, val note: String)

object VerbEngine {
    val people = listOf("Yo", "Tú", "Él / Ella / Usted", "Nosotros", "Vosotros", "Ellos / Ustedes")
    val verbs = listOf(
        Verb("hablar","говорить"), Verb("comer","есть, кушать"), Verb("vivir","жить"),
        Verb("trabajar","работать"), Verb("aprender","учиться, изучать"), Verb("estudiar","учиться"),
        Verb("ser","быть (постоянное качество)","irregular"), Verb("estar","быть, находиться","irregular"),
        Verb("ir","идти, ехать","irregular"), Verb("tener","иметь","irregular"),
        Verb("hacer","делать","irregular"), Verb("decir","говорить, сказать","irregular"),
        Verb("poder","мочь","irregular"), Verb("poner","класть, ставить","irregular"),
        Verb("venir","приходить","irregular"), Verb("querer","хотеть","irregular"),
        Verb("saber","знать","irregular"), Verb("salir","выходить","irregular"),
        Verb("ver","видеть","irregular"), Verb("dar","давать","irregular"),
        Verb("traer","приносить","irregular"), Verb("dormir","спать","irregular"),
        Verb("pedir","просить","irregular"), Verb("sentir","чувствовать","irregular"),
        Verb("conducir","водить автомобиль","irregular"), Verb("escribir","писать")
    )
    private val irregularPresent = mapOf(
        "ser" to listOf("soy","eres","es","somos","sois","son"),
        "estar" to listOf("estoy","estás","está","estamos","estáis","están"),
        "ir" to listOf("voy","vas","va","vamos","vais","van"),
        "tener" to listOf("tengo","tienes","tiene","tenemos","tenéis","tienen"),
        "hacer" to listOf("hago","haces","hace","hacemos","hacéis","hacen"),
        "decir" to listOf("digo","dices","dice","decimos","decís","dicen"),
        "poder" to listOf("puedo","puedes","puede","podemos","podéis","pueden"),
        "poner" to listOf("pongo","pones","pone","ponemos","ponéis","ponen"),
        "venir" to listOf("vengo","vienes","viene","venimos","venís","vienen"),
        "querer" to listOf("quiero","quieres","quiere","queremos","queréis","quieren"),
        "saber" to listOf("sé","sabes","sabe","sabemos","sabéis","saben"),
        "salir" to listOf("salgo","sales","sale","salimos","salís","salen"),
        "ver" to listOf("veo","ves","ve","vemos","veis","ven"),
        "dar" to listOf("doy","das","da","damos","dais","dan"),
        "traer" to listOf("traigo","traes","trae","traemos","traéis","traen"),
        "dormir" to listOf("duermo","duermes","duerme","dormimos","dormís","duermen"),
        "pedir" to listOf("pido","pides","pide","pedimos","pedís","piden"),
        "sentir" to listOf("siento","sientes","siente","sentimos","sentís","sienten"),
        "conducir" to listOf("conduzco","conduces","conduce","conducimos","conducís","conducen")
    )
    private val irregularPreterite = mapOf(
        "ser" to listOf("fui","fuiste","fue","fuimos","fuisteis","fueron"),
        "ir" to listOf("fui","fuiste","fue","fuimos","fuisteis","fueron"),
        "tener" to listOf("tuve","tuviste","tuvo","tuvimos","tuvisteis","tuvieron"),
        "estar" to listOf("estuve","estuviste","estuvo","estuvimos","estuvisteis","estuvieron"),
        "hacer" to listOf("hice","hiciste","hizo","hicimos","hicisteis","hicieron"),
        "decir" to listOf("dije","dijiste","dijo","dijimos","dijisteis","dijeron"),
        "poder" to listOf("pude","pudiste","pudo","pudimos","pudisteis","pudieron"),
        "poner" to listOf("puse","pusiste","puso","pusimos","pusisteis","pusieron"),
        "venir" to listOf("vine","viniste","vino","vinimos","vinisteis","vinieron"),
        "querer" to listOf("quise","quisiste","quiso","quisimos","quisisteis","quisieron"),
        "saber" to listOf("supe","supiste","supo","supimos","supisteis","supieron"),
        "traer" to listOf("traje","trajiste","trajo","trajimos","trajisteis","trajeron"),
        "conducir" to listOf("conduje","condujiste","condujo","condujimos","condujisteis","condujeron"),
        "dar" to listOf("di","diste","dio","dimos","disteis","dieron"),
        "ver" to listOf("vi","viste","vio","vimos","visteis","vieron")
    )
    private val irregularFutureStem = mapOf(
        "tener" to "tendr","hacer" to "har","decir" to "dir","poder" to "podr",
        "poner" to "pondr","venir" to "vendr","querer" to "querr","saber" to "sabr",
        "salir" to "saldr","haber" to "habr"
    )
    private val presentEnd = mapOf("ar" to listOf("o","as","a","amos","áis","an"),
        "er" to listOf("o","es","e","emos","éis","en"),
        "ir" to listOf("o","es","e","imos","ís","en"))
    private val preteriteEnd = mapOf("ar" to listOf("é","aste","ó","amos","asteis","aron"),
        "er" to listOf("í","iste","ió","imos","isteis","ieron"),
        "ir" to listOf("í","iste","ió","imos","isteis","ieron"))
    private val imperfectEnd = mapOf("ar" to listOf("aba","abas","aba","ábamos","abais","aban"),
        "er" to listOf("ía","ías","ía","íamos","íais","ían"),
        "ir" to listOf("ía","ías","ía","íamos","íais","ían"))
    private val futureEnd = listOf("é","ás","á","emos","éis","án")
    private val conditionalEnd = listOf("ía","ías","ía","íamos","íais","ían")
    private val subjEnd = mapOf("ar" to listOf("e","es","e","emos","éis","en"),
        "er" to listOf("a","as","a","amos","áis","an"),
        "ir" to listOf("a","as","a","amos","áis","an"))
    private fun group(v:String)=v.takeLast(2)
    private fun stem(v:String)=v.dropLast(2)
    fun participle(v:String):String {
        val irregular = mapOf("hacer" to "hecho","decir" to "dicho","ver" to "visto","poner" to "puesto",
            "escribir" to "escrito","volver" to "vuelto","abrir" to "abierto","morir" to "muerto")
        return irregular[v] ?: stem(v) + if(group(v)=="ar") "ado" else "ido"
    }
    fun gerund(v:String):String {
        val irr = mapOf("ir" to "yendo","leer" to "leyendo","oír" to "oyendo","dormir" to "durmiendo",
            "pedir" to "pidiendo","decir" to "diciendo","venir" to "viniendo")
        return irr[v] ?: stem(v) + if(group(v)=="ar") "ando" else "iendo"
    }
    private fun present(v:String):List<String> {
        irregularPresent[v]?.let { return it }
        val g=group(v); val s=stem(v)
        val ends=presentEnd[g] ?: return List(6){v}
        return ends.mapIndexed { i,e ->
            var base=s
            if (v in listOf("pensar","cerrar","empezar") && i !in listOf(3,4)) base=base.replace("e","ie")
            if (v in listOf("contar","volver","mostrar") && i !in listOf(3,4)) base=base.replace("o","ue")
            if (v in listOf("pedir","servir") && i !in listOf(3,4)) base=base.replace("e","i")
            base+e
        }
    }
    private fun imperfect(v:String)= if(v=="ser") listOf("era","eras","era","éramos","erais","eran")
        else if(v=="ir") listOf("iba","ibas","iba","íbamos","ibais","iban")
        else if(v=="ver") listOf("veía","veías","veía","veíamos","veíais","veían")
        else stem(v).let { s -> imperfectEnd[group(v)]!!.map{s+it} }
    private fun preterite(v:String):List<String> {
        irregularPreterite[v]?.let{return it}
        return preteriteEnd[group(v)]!!.map{stem(v)+it}
    }
    private fun subjPresent(v:String):List<String> {
        val special = mapOf(
            "ser" to listOf("sea","seas","sea","seamos","seáis","sean"),
            "estar" to listOf("esté","estés","esté","estemos","estéis","estén"),
            "ir" to listOf("vaya","vayas","vaya","vayamos","vayáis","vayan"),
            "haber" to listOf("haya","hayas","haya","hayamos","hayáis","hayan"),
            "saber" to listOf("sepa","sepas","sepa","sepamos","sepáis","sepan"),
            "dar" to listOf("dé","des","dé","demos","deis","den")
        )
        special[v]?.let { return it }
        val yo=present(v)[0]
        val s=yo.dropLast(1)
        val g=group(v)
        val ends=subjEnd[g]!!
        return ends.mapIndexed { i,e ->
            var base=s
            if (v in listOf("pensar","cerrar","empezar") && i !in listOf(3,4)) base=base.replace("e","ie")
            if (v in listOf("contar","volver","mostrar") && i !in listOf(3,4)) base=base.replace("o","ue")
            if (v in listOf("pedir","servir") && i !in listOf(3,4)) base=base.replace("e","i")
            base+e
        }
    }
    fun conjugations(v:String):List<Tense> {
        val p=present(v); val pret=preterite(v); val imp=imperfect(v); val part=participle(v)
        val haberPres=listOf("he","has","ha","hemos","habéis","han")
        val haberImp=listOf("había","habías","había","habíamos","habíais","habían")
        val haberFut=futureEnd.map{"habr"+it}
        val haberCond=conditionalEnd.map{"habr"+it}
        val subj=subjPresent(v)
        val impSubjBase=pret[5].removeSuffix("ron")
        val impSubjRa=listOf("ra","ras","ra","ramos","rais","ran").map{impSubjBase+it}
        val impSubjSe=listOf("se","ses","se","semos","seis","sen").map{impSubjBase+it}
        val futStem=irregularFutureStem[v] ?: v
        val future=futureEnd.map{futStem+it}
        val cond=conditionalEnd.map{futStem+it}
        return listOf(
            Tense("Pretérito Indefinido",pret,"Завершённое действие в прошлом"),
            Tense("Pretérito Perfecto",haberPres.map{"$it $part"},"Прошлое действие, связанное с настоящим"),
            Tense("Pretérito Imperfecto",imp,"Привычка, описание или процесс в прошлом"),
            Tense("Pretérito Pluscuamperfecto",haberImp.map{"$it $part"},"Действие, произошедшее раньше другого прошлого действия"),
            Tense("Subjuntivo Presente",subj,"Желание, сомнение, эмоция или оценка"),
            Tense("Subjuntivo Pretérito Perfecto",listOf("haya","hayas","haya","hayamos","hayáis","hayan").map{"$it $part"},"Предшествующее действие в контексте настоящего сослагательного наклонения"),
            Tense("Subjuntivo Pretérito Imperfecto (‑ra)",impSubjRa,"Гипотеза, желание или зависимая конструкция в прошлом"),
            Tense("Subjuntivo Pretérito Imperfecto (‑se)",impSubjSe,"Допустимый вариант формы на ‑ra"),
            Tense("Subjuntivo Pluscuamperfecto (‑ra)",listOf("hubiera","hubieras","hubiera","hubiéramos","hubierais","hubieran").map{"$it $part"},"Нереальное или предшествующее действие в прошлом"),
            Tense("Subjuntivo Pluscuamperfecto (‑se)",listOf("hubiese","hubieses","hubiese","hubiésemos","hubieseis","hubiesen").map{"$it $part"},"Вариант формы на ‑ra"),
            Tense("Futuro Simple",future,"Будущее действие"),
            Tense("Futuro Compuesto",haberFut.map{"$it $part"},"Действие, которое завершится к моменту в будущем"),
            Tense("Condicional Simple",cond,"Условие, предположение или вежливая просьба"),
            Tense("Condicional Compuesto",haberCond.map{"$it $part"},"Нереализованное действие в прошлом"),
            Tense("Gerundio",listOf(gerund(v)),"Неличная форма: действие в процессе")
        )
    }
    fun findVerb(sentence:String):Verb? {
        val words=Regex("[\\p{L}áéíóúüñÁÉÍÓÚÜÑ]+").findAll(sentence.lowercase()).map{it.value}.toList()
        val forms=verbs.flatMap { verb ->
            conjugations(verb.infinitive).flatMap { it.forms }.map { it.lowercase() to verb }
        }.groupBy({it.first},{it.second})
        for (w in words.asReversed()) {
            forms[w]?.firstOrNull()?.let { return it }
            verbs.firstOrNull { it.infinitive==w }?.let{return it}
        }
        return null
    }
    
fun ruSentence(sentence: String, verb: Verb?): String {
    if (verb == null) {
        return "Перевод предложения пока не распознан. Попробуйте глагол из встроенного словаря."
    }

    val words = mapOf(
        "yo" to "я",
        "tú" to "ты",
        "él" to "он",
        "ella" to "она",
        "nosotros" to "мы",
        "vosotros" to "вы",
        "ellos" to "они",
        "ayer" to "вчера",
        "hoy" to "сегодня",
        "mañana" to "завтра",
        "sushi" to "суши",
        "mucho" to "много",
        "bien" to "хорошо",
        "aquí" to "здесь"
    )

    val subject = sentence.lowercase()
        .split(Regex("\\s+"))
        .firstOrNull()
        ?.trim('.', ',', '!', '?', '¿', '¡')
        ?.let { words[it] }

    return (if (subject != null) "$subject · " else "") +
        "${verb.ru}. Перевод остальных слов зависит от контекста."
}
