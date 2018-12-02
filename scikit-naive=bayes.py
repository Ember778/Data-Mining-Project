import numpy as np
from glob import glob
from sklearn import datasets
from sklearn.model_selection import train_test_split
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.feature_extraction.text import TfidfTransformer
from sklearn.linear_model import SGDClassifier
from sklearn import metrics
from sklearn.pipeline import Pipeline

categories = ['Mail','Spam']

training_data = datasets.load_files(r'C:\Users\jeffr\Data-Mining-Files\train',
                                   description=None, categories=categories,
                                   load_content=True, encoding='utf-8',
                                   shuffle=False)

testing_data = datasets.load_files(r'C:\Users\jeffr\Data-Mining-Files\test',
                                   description=None, categories=categories,
                                   load_content=True, encoding='utf-8',
                                   shuffle=False)
X_train = training_data.data
y_train = training_data.target
X_test = testing_data.data
y_test = testing_data.target


vectorizer = CountVectorizer(stop_words='english')
X_train_counts = vectorizer.fit_transform(raw_documents=X_train)

tfidf_transformer = TfidfTransformer(use_idf=True)

X_train_tfidf = tfidf_transformer.fit_transform(X_train_counts)

count_vect = CountVectorizer(stop_words='english')
X_test_counts = count_vect.fit_transform(raw_documents=X_test)

tfidf_transformer = TfidfTransformer(use_idf=True)
X_test_tfidf = tfidf_transformer.fit_transform(X_test_counts)

text_clf = Pipeline([('vect', CountVectorizer(stop_words='english')),
    ('tfidf', TfidfTransformer(use_idf=True)),
    ('clf', SGDClassifier(loss='hinge', penalty='l2', alpha=1e-3, random_state=42, 
    verbose=1)),])

text_clf.fit(X_train, y_train)

predicted = text_clf.predict(X_test)

print (np.mean(predicted == y_test))

print(metrics.classification_report(y_test, predicted, 
    target_names=training_data.target_names))                                               

