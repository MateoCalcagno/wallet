import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useTransactionHistory } from '../hooks/useTransactionHistory'
import { getIconConfig, getLabel, getAmountColor, getAmountPrefix, formatDate } from '../utils/transactionHelpers'
import AppLayout from '../components/AppLayout'

const FILTERS = [
  { label: 'Todos',          value: '' },
  { label: 'Depósitos',      value: 'DEPOSIT' },
  { label: 'Transferencias', value: 'TRANSFER' },
  { label: 'Retiros',        value: 'WITHDRAWAL' },
]

const LIST_HEIGHT = 430

function TransactionHistory() {
  const navigate = useNavigate()
  const [type, setType] = useState('')
  const { history, page, setPage, totalPages, historyError, isLoading } = useTransactionHistory({ size: 7, type })

  return (
    <AppLayout>
      <div className="p-5 flex flex-col gap-4">

        {/* Header + Filtros */}
        <div className="pb-3 border-b border-gray-100 pl-3">
          <button
            onClick={() => navigate('/dashboard')}
            className="flex items-center gap-1.5 text-xs text-gray-400 font-medium hover:text-blue-400 transition cursor-pointer mb-4"
          >
            <svg xmlns="http://www.w3.org/2000/svg" className="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M15 19l-7-7 7-7" />
            </svg>
            Volver a Inicio
          </button>
          <div className="flex items-end justify-between">
            <div>
              <h1 className="text-4xl font-bold text-slate-800 leading-none tracking-tight">Movimientos</h1>
              <p className="text-sm text-blue-400 mt-1">Historial completo de tu cuenta</p>
            </div>

            <div className="flex gap-2 pb-1">
              {FILTERS.map((f) => (
                <button
                  key={f.value}
                  onClick={() => setType(f.value)}
                  className={`px-4 py-2 rounded-lg text-sm font-medium transition cursor-pointer ${
                    type === f.value
                      ? 'bg-slate-900 text-white'
                      : 'bg-blue-50 border border-blue-100 text-blue-400 hover:border-blue-200'
                  }`}
                >
                  {f.label}
                </button>
              ))}
            </div>
          </div>
        </div>

        {/* Error */}
        {historyError && (
          <div className="px-4 py-3 bg-red-50 border border-red-200 rounded-lg text-sm text-red-600">
            {historyError}
          </div>
        )}

        {/* Lista */}
        <div>
          <div
            className="bg-blue-50 rounded-xl border border-blue-100 overflow-hidden"
            style={{ minHeight: `${LIST_HEIGHT}px` }}
          >
            {isLoading ? (
              <div className="flex flex-col gap-2 p-3">
                {[...Array(7)].map((_, i) => (
                  <div key={i} className="h-12 bg-blue-100 rounded-lg animate-pulse" />
                ))}
              </div>
            ) : history.length === 0 ? (
              <p className="text-sm text-blue-300 text-center py-12">No hay movimientos para mostrar.</p>
            ) : (
              history.map((t) => {
                const { bg, color, icon } = getIconConfig(t)
                return (
                  <div key={t.id} className="flex justify-between items-center px-4 py-3 border-b border-blue-100/60 last:border-0">
                    <div className="flex items-center gap-3">
                      <div className={`w-8 h-8 ${bg} rounded-lg flex items-center justify-center shrink-0`}>
                        <svg xmlns="http://www.w3.org/2000/svg" className={`w-4 h-4 ${color}`} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                          <path strokeLinecap="round" strokeLinejoin="round" d={icon} />
                        </svg>
                      </div>
                      <div>
                        <p className="text-sm font-medium text-gray-800">{getLabel(t)}</p>
                        <p className="text-xs text-blue-300">{formatDate(t.createdAt)}</p>
                      </div>
                    </div>
                    <span className={`text-sm font-medium ${getAmountColor(t)}`}>
                      {getAmountPrefix(t)}${Number(t.amount).toLocaleString('es-AR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                    </span>
                  </div>
                )
              })
            )}
          </div>
        </div>

        {/* Paginación */}
        <div className="flex items-center justify-center gap-5 p-2">
          <button
            disabled={page === 0}
            onClick={() => setPage(page - 1)}
            className="px-4 py-2 bg-blue-500 hover:bg-blue-600 text-white rounded-lg text-sm font-medium transition disabled:bg-blue-300 disabled:cursor-not-allowed cursor-pointer"
          >
            Anterior
          </button>

          <div className="px-4 py-2 bg-slate-900 rounded-lg">
            <span className="text-sm font-medium text-white">{page + 1}</span>
            <span className="text-sm text-slate-400 mx-1">/</span>
            <span className="text-sm text-slate-300">{totalPages}</span>
          </div>

          <button
            disabled={page + 1 >= totalPages}
            onClick={() => setPage(page + 1)}
            className="px-4 py-2 bg-blue-500 hover:bg-blue-600 text-white rounded-lg text-sm font-medium transition disabled:bg-blue-300 disabled:cursor-not-allowed cursor-pointer"
          >
            Siguiente
          </button>
        </div>

      </div>
    </AppLayout>
  )
}

export default TransactionHistory