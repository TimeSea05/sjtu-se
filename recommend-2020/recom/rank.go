package recom

import (
	"fmt"
	"sort"
)

// store the ranking of keywords.
// the more times a keyword appears, the higher its ranking
// and the closer it is to the front of the array.
var rankings Records

// counts the total number of times each keyword has been
// searched by all users
var keywordsCount = make(map[string]int)

func RankKeywords() {
	for _, records := range userKeywordsCounts {
		for _, record := range records {
			keywordsCount[record.keyword] += record.count
		}
	}

	for keyword, count := range keywordsCount {
		rankings = append(rankings, Record{keyword: keyword, count: count})
	}
	sort.Sort(rankings)

	fmt.Println("\nTop Five Keywords and Search Counts: ")
	for IDx, record := range rankings {
		if IDx >= 5 {
			break
		}

		fmt.Printf("Keyword: %s; Count: %d\n", record.keyword, record.count)
	}
}
