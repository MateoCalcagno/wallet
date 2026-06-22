// src/components/WelcomeSplash.jsx
import { useEffect, useState } from 'react'
import NovaLogo from './NovaLogo'

export default function WelcomeSplash({ onStart, onSkip }) {
  const [visible, setVisible] = useState(false)

  useEffect(() => {
    const t = setTimeout(() => setVisible(true), 50)
    return () => clearTimeout(t)
  }, [])

  return (
    <div
      className={`fixed inset-0 z-[100] flex flex-col items-center justify-center transition-opacity duration-500 ${visible ? 'opacity-100' : 'opacity-0'}`}
      style={{ background: 'linear-gradient(135deg, #0f172a 0%, #1e3a5f 60%, #0f172a 100%)' }}
    >
      <div className="absolute top-16 right-16 w-40 h-40 rounded-full bg-blue-700 opacity-20 blur-2xl pointer-events-none" />
      <div className="absolute bottom-24 left-12 w-32 h-32 rounded-full bg-blue-500 opacity-15 blur-2xl pointer-events-none" />

      <div className="flex flex-col items-center gap-8 select-none px-8 max-w-sm w-full">

        <div className="scale-[1.8]">
          <NovaLogo />
        </div>

        <div className="flex flex-col items-center gap-2 mt-6">
            <p className="text-white text-2xl font-bold tracking-widest">BIENVENIDO</p>
        </div>

        <p className="text-slate-400 text-sm text-center leading-relaxed mt-2">
          ¿Te mostramos cómo funciona todo?
        </p>

        <div className="flex flex-col gap-3 w-full mt-4">
          <button
            onClick={onStart}
            className="w-full py-3 bg-blue-500 hover:bg-blue-600 text-white rounded-xl text-sm font-medium transition cursor-pointer"
          >
            Arrancar tour →
          </button>
          <button
            onClick={onSkip}
            className="w-full py-2 text-slate-500 hover:text-slate-300 text-sm transition cursor-pointer"
          >
            Saltar
          </button>
        </div>

      </div>
    </div>
  )
}