package recom

import (
	"bytes"
	"fmt"
	"io"
	"os"
	"sort"
	"strconv"
)

type BinaryRelation struct {
	relation int
	userAID  int
	userBID  int
}

type BinaryRelations []BinaryRelation

// implement sort.Interface to use `sort.Sort`
func (r BinaryRelations) Len() int {
	return len(r)
}

func (r BinaryRelations) Less(i, j int) bool {
	return r[i].relation > r[j].relation ||
		(r[i].relation == r[j].relation && r[i].userAID < r[j].userAID) ||
		(r[i].relation == r[j].relation && r[i].userAID == r[j].userAID && r[i].userBID < r[j].userBID)
}

func (r BinaryRelations) Swap(i, j int) {
	r[i], r[j] = r[j], r[i]
}

// use this struct when one user is already fixed
type SingleRelation struct {
	relation int
	userID   int
}

type SingleRelations []SingleRelation

// implement sort.Interface to use `sort.Sort`
func (r SingleRelations) Len() int {
	return len(r)
}

func (r SingleRelations) Less(i, j int) bool {
	return r[i].relation > r[j].relation ||
		(r[i].relation == r[j].relation && r[i].userID < r[j].userID)
}

func (r SingleRelations) Swap(i, j int) {
	r[i], r[j] = r[j], r[i]
}

var relations [][]int
var binaryRelations BinaryRelations

func CalcRelations() {
	relations = make([][]int, 0)
	for i := 0; i < totalUserNum; i++ {
		relations = append(relations, make([]int, totalUserNum))
	}

	for i := 0; i < totalUserNum; i++ {
		for j := totalUserNum - 1; j > i; j-- {
			userAID := userIDs[i]
			userBID := userIDs[j]

			for idx, record := range rankings { // for each keyword
				keyword := record.keyword // k
				var countA int            // Count(A, k)
				var countB int            // Count(B, k)

				for _, recordA := range userKeywordsCounts[userAID] {
					if recordA.keyword == keyword {
						countA = recordA.count
						break
					}
				}

				for _, recordB := range userKeywordsCounts[userBID] {
					if recordB.keyword == keyword {
						countB = recordB.count
					}
				}

				count := minInt(countA, countB)   // min(Count(A, k), Count(B, k))
				weight := len(rankings) - idx     // Weights(k)
				relations[i][j] += count * weight // \sum_k(min(Count(A, k), Count(B, k)) * Weights(k))
				relations[j][i] = relations[i][j] // symmetric
			}

			binaryRelations = append(binaryRelations, BinaryRelation{
				relation: relations[i][j],
				userAID:  minInt(userAID, userBID),
				userBID:  maxInt(userAID, userBID),
			})
		}
	}

	sort.Sort(binaryRelations)
	fmt.Println("\nThe Mostly Related Users and Degree of Relation:")
	fmt.Printf("User1: %d, User2: %d, Relation: %d\n",
		binaryRelations[0].userAID, binaryRelations[0].userBID, binaryRelations[0].relation)
}

func CalcRecom() error {
	recomFile, err := os.Create("recommendations.txt")
	if err != nil {
		return err
	}

	for i := 0; i < totalUserNum; i++ {
		curUserID := userIDs[i] // currentUser: A

		// get the `relation` values between user A and all other users
		var singleRelations SingleRelations
		for j := 0; j < totalUserNum; j++ {
			if j == i {
				continue
			}

			singleRelations = append(singleRelations, SingleRelation{
				relation: relations[i][j],
				userID:   userIDs[j],
			})
		}

		// sort the `relation` values
		sort.Sort(singleRelations)

		// after sorting, the first three elements in the array
		// are the top three users(B, C, D) who have the highest degree of
		// association with user A
		var set map[string]bool = make(map[string]bool)
		for singleRelationIdx, singleRelation := range singleRelations {
			if singleRelationIdx >= 3 {
				break
			}

			// calculate the union of the keywords searched by users B, C, and D.
			records := userKeywordsCounts[singleRelation.userID]
			for _, record := range records {
				set[record.keyword] = true
			}
		}

		// remove the keywords searched by user A from the union set.
		curUserRecords := userKeywordsCounts[curUserID]
		for _, record := range curUserRecords {
			delete(set, record.keyword)
		}

		// find the top three keywords with the highest ranking
		// among the remaining keywords
		var recomRecords Records
		for keyword := range set {
			recomRecords = append(recomRecords, Record{
				keyword: keyword,
				count:   keywordsCount[keyword],
			})
		}
		sort.Sort(recomRecords)

		var buf bytes.Buffer
		buf.WriteString(strconv.Itoa(curUserID))

		for idx, record := range recomRecords {
			if idx >= 3 {
				break
			}
			buf.WriteString(" ")
			buf.WriteString(record.keyword)
		}
		buf.WriteString("\n")

		io.WriteString(recomFile, buf.String())
	}

	return nil
}
