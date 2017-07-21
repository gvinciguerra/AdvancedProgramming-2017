#!/bin/bash

# Constraint generator for Sudoku with 2x2 regions

N=2

# Variables
for (( i = 1; i <= N*N; i++ )); do
    for (( j = 1; j <= N*N; j++ )); do
        printf "x$i$j = {"
        for (( n = 1; n <= N*N; n++ )); do
            if (( n == 1 )); then
                printf "x$i$j@$n"
            else
                printf ", x$i$j@$n"
            fi
        done
        printf "}\\n"
    done
done
printf "{}\\n!{"

# Same row
for (( i = 1; i <= N*N; i++ )); do
    for (( n = 1; n <= N*N; n++ )); do
        for (( j = 1; j <= N*N; j++ )); do
            for (( k = 1; k <= N*N; k++ )); do
                if (( j != k)); then
                    printf "(x$i$j@$n, x$i$k@$n), "
                fi
            done
        done
    done
done

# Same column
for (( n = 1; n <= N*N; n++ )); do
    for (( j = 1; j <= N*N; j++ )); do
        for (( k = 1; k <= N*N; k++ )); do
            for (( i = 1; i <= N*N; i++ )); do
                if (( i != k )); then
                    printf "(x$k$j@$n, x$i$j@$n), "
                fi
            done
        done
    done
done

# Diagonal
for (( l = 1; l <= N*N; l+=N )); do
    for (( m = 1; m <= N*N; m+=N )); do
        for (( n = 1; n <= N*N; n++ )); do
            for (( i = 0; i < N; i++ )); do
                for (( j = 0; j < N; j++ )); do
                    s=$((l+i))
                    t=$((m+i))
                    u=$((l+j))
                    v=$((m+j))
                    if ((s != u || t != v)); then
                        printf "(x$s$t@$n, x$u$v@$n), "
                    fi
                done
            done
        done
    done
done

# Antidiagonal
{
    for (( l = 1; l <= N*N; l+=N )); do
        for (( m = 1; m <= N*N; m+=N )); do
            for (( n = 1; n <= N*N; n++ )); do
                for (( i = 0; i < N; i++ )); do
                    for (( j = 0; j < N; j++ )); do
                        s=$((l+i))
                        t=$((m+N-i-1))
                        u=$((l+j))
                        v=$((m+N-j-1))
                        if ((s != u || t != v)); then
                            printf "(x$s$t@$n, x$u$v@$n), "
                        fi
                    done
                done
            done
        done
    done
} | sed 's/, $/}/'
