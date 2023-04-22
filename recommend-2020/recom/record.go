package recom

import (
	"fmt"
	"io"
	"os"
	"sort"
	"strconv"
	"strings"
)

type Record struct {
	keyword string
	count   int
}

type Records []Record

// implement sort.Interface to use `sort.Sort`
func (r Records) Len() int {
	return len(r)
}

func (r Records) Less(i, j int) bool {
	return r[i].count > r[j].count ||
		(r[i].count == r[j].count && r[i].keyword < r[j].keyword)
}

func (r Records) Swap(i, j int) {
	r[i], r[j] = r[j], r[i]
}

// counts the total number of times each keyword has been
// searched by each user
// userid -> []Record{{"SJTU", 1}, ...}
var userKeywordsCounts = make(map[int][]Record)

var userIDs []int
var totalUserNum int
var totalRecordNum int

func LoadKeywordCounts(logFileNames []string) error {
	fmt.Println("Reading Log Files...")
	for _, logFileName := range logFileNames {
		logFile, err := os.Open(logFileName)
		if err != nil {
			return err
		}

		logFileContent, err := io.ReadAll(logFile)
		if err != nil {
			return err
		}

		records := strings.Split(string(logFileContent), "\n")
		records = records[0 : len(records)-1]
		totalRecordNum += len(records)

		for _, record := range records {
			recordItems := strings.Split(record, " ")
			userID, _ := strconv.Atoi(recordItems[0])
			keyword := recordItems[1]

			_, userExists := userKeywordsCounts[userID]
			if !userExists {
				userIDs = append(userIDs, userID)
				totalUserNum++
			}

			// first, find out if the keyword already exists in userKeywordsCount[userID](type: []Record)
			keywordExists := false
			for IDx, record := range userKeywordsCounts[userID] {
				if record.keyword == keyword {
					keywordExists = true
					userKeywordsCounts[userID][IDx].count++
					break
				}
			}

			// if the keyword does not exist in userKeywordsCount[userID]
			// append a new record to userKeywordsCount[userID]
			if !keywordExists {
				userKeywordsCounts[userID] = append(userKeywordsCounts[userID], Record{keyword: keyword, count: 1})
			}

		}
		fmt.Printf("Read Log File: %s\n", logFileName)
		fmt.Printf("Total Users: %d, Total Records: %d\n", totalUserNum, totalRecordNum)
	}

	sort.Ints(userIDs)
	return nil
}
