#!/usr/local/bin/python
import argparse
import sys

def verify_args(cmd_args):
    """parse arguments and verify if they are correct"""
    parser = argparse.ArgumentParser(description='Keyword In Context')
    parser.add_argument('-f', '--file', metavar='file_path', help='The absolute \
            path of the file', nargs=1, type=file, dest='filePath', required=True)
    parser.add_argument('-s', '--size', metavar='ctxt_length', help='The \
            length of the context around the keyword', nargs=1, type=int, default=5)
    parser.add_argument('-k', '--keyword', metavar='keyword', help='The keyword \
            to be searched', nargs=1, type=str, required=True)

    args = parser.parse_args(cmd_args)
    return args

def display(output, ctxt_size, kw_len):
    """Display the kwic output"""
    for left_ctxt, keyw, right_ctxt in output:
        print "{0:{3}}\t{1:{4}}\t{2:{3}}".format(left_ctxt, keyw, right_ctxt, 5*ctxt_size, kw_len)

def launch_kwic(file_obj, size, keyword):
    """Perform keyword search and identify the contexts"""
    if size < 0:
        raise ValueError("Context length has to be non-zero positive integer")
    token_chain = []
    output = []
    for line in file_obj:
        token_chain.extend(line.strip().split())
    begin = 0
    end = len(token_chain)
    while token_chain[begin:].count(keyword) > 0:
        idx = token_chain[begin:].index(keyword)
        start_ctxt = ''
        end_ctxt = ''
        if idx-1 < 0:
            pass
        elif idx-size < 0:
            start_ctxt = " ".join(token_chain[:idx-1])
        else:
            start_ctxt = " ".join(token_chain[idx-size:idx-1])
        if idx+1 >= end:
            pass
        elif idx+size >= end:
            end_ctxt = " ".join(token_chain[idx+1:])
        else:
            end_ctxt = " ".join(token_chain[idx+1:idx+size])
        output.append((start_ctxt, keyword, end_ctxt))
        begin += idx+1
    file_obj.close()
    return output

if __name__ == '__main__':
    try:
        PARSED_ARGS = verify_args(sys.argv[1:])
        CONCORD = launch_kwic(PARSED_ARGS.filePath[0], PARSED_ARGS.size[0], PARSED_ARGS.keyword[0])
        display(CONCORD, PARSED_ARGS.size[0], len(PARSED_ARGS.keyword[0]))
    except IOError as ioe:
        print ioe
    except ValueError as val_err:
        print val_err
        #print sys.exc_info()[1][1]
