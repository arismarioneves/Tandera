package com.mari05lim.tandera.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Respostas reais da API Tandera v2.2 (encurtadas): o app precisa ler exatamente esse formato. */
class ApiContractTest {

    @Test
    fun `series details with null movie fields and unknown values decode`() {
        val json = """
            {"encontrado":true,"id":1396,"tipo":"serie","titulo":"Breaking Bad","titulo_original":"Breaking Bad",
             "ano":2008,"poster":"https://image.tmdb.org/t/p/w342/a.jpg","nota":8.9,"sinopse":"…",
             "fundo":null,"generos":["Drama","Crime"],"duracao":null,"temporadas":5,
             "trailer":"https://www.youtube.com/watch?v=VFkjBy2b50Q","regiao":"BR",
             "stream":[{"name":"Netflix","link":"https://www.netflix.com/search?q=Breaking%20Bad","link_tipo":"busca",
                        "logo":"https://image.tmdb.org/t/p/w154/n.jpg","modalidades":["assinatura","modalidade_futura"]}],
             "mais_opcoes":"https://www.themoviedb.org/tv/1396/watch?locale=BR","alternativas":[],"fonte":"…","campo_novo":1}
        """.trimIndent()

        val details = TanderaJson.decodeFromString<TitleDetails>(json)

        assertEquals(MediaType.Series, details.type)
        assertNull(details.runtimeMinutes)
        assertEquals(5, details.seasons)
        assertEquals(listOf("assinatura", "modalidade_futura"), details.offers.single().modalities)
        assertEquals(Modality.Subscription, Modality.fromApi(details.offers.single().modalities[0]))
        assertNull(Modality.fromApi("modalidade_futura"))
    }

    @Test
    fun `catalog page decodes items without rating`() {
        val json = """
            {"lista":"em_breve","tipo":"filme","regiao":"BR","pagina":2,"total_paginas":4,"resultados":[
              {"id":1,"tipo":"filme","titulo":"Sem votos","titulo_original":"No votes","ano":2026,"poster":null,"nota":null}]}
        """.trimIndent()

        val page = TanderaJson.decodeFromString<CatalogPage>(json)

        assertEquals(2, page.page)
        assertEquals(4, page.totalPages)
        assertNull(page.results.single().rating)
        assertNull(page.results.single().posterUrl)
    }
}
