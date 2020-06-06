#!/usr/bin/env python2
# -*- coding: utf-8 -*-

import argparse
import string
import sys

SIZE = 5

def display_kwic(output, size=SIZE):
    """Display the kwic output"""
    for left_ctxt, keyword, right_ctxt in output:
        print("{0:{3}}\t{1:{4}}\t{2:{3}}".format(left_ctxt, keyword, right_ctxt, 5*size, len(keyword)))

def launch_kwic(file_obj, keyword, size=SIZE, ignore_case=False):
    """Perform keyword search and identify the contexts"""
    if size < 1:
        raise ValueError('Error: context length has to a be non-zero positive integer.')

    size += 1
    output = []

    if ignore_case:
        keyword = keyword.lower()

    for line in file_obj:
        token_chain = []
        original_chain = line.strip().split()

        if ignore_case:
            line = line.lower()

        token_chain.extend(line.strip().split())

        for i, token in enumerate(token_chain):
            if keyword in token:
                for punct in string.punctuation:
                    token_chain[i] = token_chain[i].strip(punct)

        while token_chain.count(keyword) > 0:
            idx = token_chain.index(keyword)
            end = len(token_chain)

            start_ctxt = str()
            end_ctxt = str()

            if idx-1 < 0:
                pass
            elif idx-size < 0:
                start_ctxt = ' '.join(original_chain[:idx-1])
            else:
                start_ctxt = ' '.join(original_chain[idx-size+1:idx])

            if idx+1 >= end:
                pass
            elif idx+size >= end:
                end_ctxt = ' '.join(original_chain[idx+1:])
            else:
                end_ctxt = ' '.join(original_chain[idx+1:idx+size])

            if len(original_chain[idx]) > len(token_chain[idx]):
                head, tail = original_chain[idx].lower().split(token_chain[idx].lower(),1)
                original_chain[idx] = original_chain[idx].lstrip(head).rstrip(tail)
                start_ctxt = str(start_ctxt + ' ' + head).lstrip()
                end_ctxt = str(tail + ' ' + end_ctxt).rstrip()

            output.append([start_ctxt, original_chain[idx], end_ctxt])
            original_chain = original_chain[idx+1:]
            token_chain = token_chain[idx+1:]

    return output

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('file', help='absolute path of the file', type=file)
    parser.add_argument('keyword', metavar='keyword', help='keyword to search (comma separated')
    parser.add_argument('-s', '--size', help='length of the context around the keyword', default=SIZE, type=int)
    parser.add_argument('-i', '--ignore-case', action='store_true', help='ignore case letters (AaBbCc)')
    args = parser.parse_args()
    kwic = launch_kwic(args.file, args.keyword, args.size, args.ignore_case)
    display_kwic(kwic)
