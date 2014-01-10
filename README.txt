
The programs includes 5 main files:



main.java
which given an input file indexes the file and creates two database files: Inverted_Index_Table and
Inverted_Index_Table.

main_UserInterface.java
which performs the actual search on the database file and display the result back to the user

newsItemsBytes.java
represent a document object. Document id is starting byte address of a newsitem tage

Word.java
represent a word object



Inverted_Index_Table:
Has the following structure:

key                data
term               list of all document ids, the term was found in

In the system the list looks like this:

[word=coal,        list=[181006-184022, 296608-298519, 1076525-1080122, 1161090-1167350, 1167409-1173671,
1850393-1851831, 2068844-2070806, 2209962-2211780,.........



Document_Vector_Table
key                data
doc id           list of all the words which were found in the document and their associated log frequency

In the system the list looks like this:

4089221-4094680    [word=france logFreqNormalized=0.075], [word=police logFreqNormalized=0.106], [word=end logFreqNormalized=0.066], [word=african logFreqNormalized=0.075],...............


How the system perform a search:
Step 1: Boolean retrieval:

when user enters a query, the system using the Inverted_Index_Table, first finds a list of all the documents(doc id)
which any of the query word was found in.

Step 2: Calculating Similarity Measure:
then the system  using the Document_Vector_Table, and cosine sumilarity measure, sorts these documents
according to the level of relevance to the query
