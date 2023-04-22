package main

import (
	"log"
	"recommend-2020/recom"
)

func main() {
	// logFileNames := []string{"small-case/keywords-1.log", "small-case/keywords-2.log", "small-case/keywords-3.log"}
	logFileNames := []string{"demo/keywords-1.log", "demo/keywords-2.log", "demo/keywords-3.log"}
	if err := recom.LoadKeywordCounts(logFileNames); err != nil {
		log.Fatal(err)
	}

	recom.RankKeywords()
	recom.CalcRelations()
	if err := recom.CalcRecom(); err != nil {
		log.Fatal(err)
	}
}
